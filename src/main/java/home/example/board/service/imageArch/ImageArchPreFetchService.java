package home.example.board.service.imageArch;

import home.example.board.DTO.imageArch.ImagePostRequestDTO;
import home.example.board.config.MinioConfig;
import home.example.board.dao.imageArch.ImageDAO;
import home.example.board.dao.imageArch.ImagePostDAO;
import home.example.board.domain.Image;
import home.example.board.domain.ImagePost;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImageArchPreFetchService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final ImagePostDAO imagePostDAO;
    private final ImageDAO imageDAO;
    private final MinioConfig minioConfig;
    private final MinioClient minioClient;
    private final long REDIS_EXPIRE_TIME = 60 * 60 * 24; // 1 day
    Duration REDIS_EXPIRE_DURATION = Duration.ofSeconds(REDIS_EXPIRE_TIME);

    @Autowired
    public ImageArchPreFetchService(
            ImagePostDAO imagePostDAO, ImageDAO imageDAO,
            MinioConfig minioConfig) {
        this.imagePostDAO = imagePostDAO;
        this.imageDAO = imageDAO;
        this.minioConfig = minioConfig;
        this.minioClient = this.minioConfig.minioClient();
    }

    // Pre-fetch thumbnail images for image posts
    public List<Image> preFetchImagePosts(ImagePostRequestDTO imagePostRequestDTO) {
        List<ImagePost> imagePosts = selectImagePostList(imagePostRequestDTO);
        List<Image> images = selectImageList(imagePosts);
        preFetchImagePostList(images);
        return images;
    }

    private List<ImagePost> selectImagePostList(ImagePostRequestDTO imagePostRequestDTO) {
        // Fetch image posts with pagination
        List<ImagePost> imagePostList = imagePostDAO.getImagePostList(imagePostRequestDTO);
        return imagePostList != null && !imagePostList.isEmpty() ? imagePostList : new ArrayList<>();
    }

    private List<Image> selectImageList(List<ImagePost> imagePosts) {
        // Fetch images associated with the post IDs
        List<Long> postIds = imagePosts.stream()
                .map(ImagePost::getPost_id)
                .collect(Collectors.toList());
        List<Image> images = imageDAO.selectImageList(postIds);
        // pick only one image by post_id
        return new ArrayList<>(images.stream()
                .collect(Collectors.toMap(Image::getPost_id, image -> image, (existing, replacement) -> existing))
                .values());
    }

    private void preFetchImagePostList(List<Image> images) {
        if(images == null || images.isEmpty()) return;

        for(Image image : images) {
            String thumbnailUrl = image.getThumb_url();
            String imageEncodedUrl = Base64.getEncoder().encodeToString(thumbnailUrl.getBytes(StandardCharsets.UTF_8));
            String redisKey = "image:" + imageEncodedUrl;

            String minioPath = stringRedisTemplate.opsForValue().get(redisKey);
            if (minioPath == null || minioPath.isEmpty()) {
                // Fetch the image bytes from the URL
                ResponseEntity<byte[]> imageResponse = fetchImageFromSource(thumbnailUrl);
                if(imageResponse.getStatusCode().is2xxSuccessful()) {
                    byte[] imageBytes = imageResponse.getBody();
                    String fileExtension = extractExt(thumbnailUrl, imageResponse.getHeaders().getContentType());
                    String minioFileName = UUID.randomUUID()+fileExtension;
                    String contentType = detectMimeType(imageBytes, minioFileName);
                    try {
                        if(imageBytes != null) {
                            minioClient.putObject(
                                    PutObjectArgs.builder()
                                            .bucket(minioConfig.getBucketName())
                                            .object(minioFileName)
                                            .stream(new ByteArrayInputStream(imageBytes), imageBytes.length, -1)
                                            .contentType(contentType) // Adjust content type as needed
                                            .build()
                            );
                            stringRedisTemplate.opsForValue().set(redisKey, minioFileName.trim(), REDIS_EXPIRE_DURATION);
                        }
                    } catch (Exception e) {
                        log.error("minio image upload error {}",e.getMessage());
                    }
                }else{
                    log.error("image proxy error {}",imageResponse.getStatusCode());
                }
            }
        }
    }

    private ResponseEntity<byte[]> fetchImageFromSource(String imgUrl) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        headers.set("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.set("Referer", getRefererFromImgUrl(imgUrl));

        try {
            return restTemplate.exchange(URI.create(imgUrl), HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    private String getRefererFromImgUrl(String imgUrl) {
        try {
            URI uri = URI.create(imgUrl);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            return scheme + "://" + host + "/";
        } catch (Exception e) {
            return "";
        }
    }

    private String detectMimeType(byte[] data, String filename) {
        try {
            Tika tika = new Tika();
            String mime = tika.detect(data, filename);
            if (mime == null || mime.trim().isEmpty()) return "application/octet-stream";
            return mime;
        } catch (Exception e) {
            filename = filename.toLowerCase(Locale.ROOT);
            if (filename.endsWith(".png")) return "image/png";
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
            if (filename.endsWith(".gif")) return "image/gif";
            return "application/octet-stream";
        }
    }

    private String extractExt(String url, MediaType mediaType) {
        int idx = url.lastIndexOf('.');
        if (idx > 0 && idx < url.length() - 1) {
            String ext = url.substring(idx).toLowerCase(Locale.ROOT);
            if (Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp").contains(ext)) return ext;
        }
        if (mediaType != null) {
            String mt = mediaType.toString();
            if ("image/png".equals(mt)) return ".png";
            if ("image/gif".equals(mt)) return ".gif";
            if ("image/webp".equals(mt)) return ".webp";
            if ("image/bmp".equals(mt)) return ".bmp";
        }
        return ".jpg";
    }

    private byte[] toBytes(InputStream is) throws Exception {
        return org.apache.commons.io.IOUtils.toByteArray(is);
    }
}
