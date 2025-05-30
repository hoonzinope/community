package home.example.board.service.imageArch;

import home.example.board.DTO.imageArch.ImagePostRequestDTO;
import home.example.board.config.MinioConfig;
import home.example.board.dao.imageArch.ImageDAO;
import home.example.board.dao.imageArch.ImageFetchDAO;
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
    private final ImageFetchDAO imageFetchDAO;

    @Autowired
    public ImageArchPreFetchService(
            ImagePostDAO imagePostDAO, ImageDAO imageDAO,
            MinioConfig minioConfig, ImageFetchDAO imageFetchDAO) {
        this.imagePostDAO = imagePostDAO;
        this.imageDAO = imageDAO;
        this.minioConfig = minioConfig;
        this.minioClient = this.minioConfig.minioClient();
        this.imageFetchDAO = imageFetchDAO;
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
                imageFetchDAO.saveImageFromExternal(imageEncodedUrl);
            }
        }
    }
}
