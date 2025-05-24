package home.example.board.controller.api.imgArch;

import home.example.board.config.MinioConfig;
import home.example.board.config.RedisConfig;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Time;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@RestController
public class ImageProxyAPI {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final MinioConfig minioConfig;
    private final MinioClient minioClient;
    private final long REDIS_EXPIRE_TIME = 60 * 60 * 24; // 1 day
    Duration REDIS_EXPIRE_DURATION = Duration.ofSeconds(REDIS_EXPIRE_TIME);

    @Autowired
    public ImageProxyAPI(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
        this.minioClient = this.minioConfig.minioClient();
    }

    @Operation(summary = "이미지 프록시", description = "이미지 인코딩된 URL을 통해 이미지를 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "이미지 프록시 성공",
                    content = {
                            @Content(
                                    mediaType = "image/*",
                                    examples = {
                                            @ExampleObject(
                                                    name = "정상 response",
                                                    value = "byte[] (이미지 바이너리 데이터)"
                                            )
                                    }
                            )
                    }),
            @ApiResponse(
                    responseCode="400",
                    description = "이미지 프록시 실패",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 response",
                                                    value = "null (이미지 바이너리 데이터 없음)"
                                            )
                                    }
                            )
                    })
    })
    @GetMapping("/api/image-proxy/")
    public ResponseEntity<byte[]> getImage(
            @Parameter(description = "이미지 encode URL", required = true)
            @RequestParam(required = true, value = "url") String imageEncodedUrl) {
        // Decode the URL
        String imageUrl = new String(Base64.getDecoder().decode(imageEncodedUrl));
        String redisKey = "image:" + imageEncodedUrl;

        String minioPath = stringRedisTemplate.opsForValue().get(redisKey);
        if (minioPath != null && !minioPath.isEmpty()) {
            // If the image is in Redis, get image from minio
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(minioPath)
                            .build())) {
                byte[] imageBytes = toBytes(inputStream);
                String contentType = detectMimeType(imageBytes, minioPath);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))// Adjust content type as needed
                        .body(imageBytes);
            } catch (Exception e) {
                log.error("minio image not found {}",e.getMessage());
                stringRedisTemplate.delete(redisKey);
            }
        }else{
            // Fetch the image bytes from the URL
            ResponseEntity<byte[]> imageResponse = fetchImageFromSource(imageUrl);
            if(imageResponse.getStatusCode().is2xxSuccessful()) {
                byte[] imageBytes = imageResponse.getBody();
                String fileExtension = extractExt(imageUrl, imageResponse.getHeaders().getContentType());
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
                        return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(contentType))// Adjust content type as needed
                                .body(imageBytes);
                    }
                } catch (Exception e) {
                    log.error("minio image upload error {}",e.getMessage());
                    return ResponseEntity.status(500).body(null);
                }
            }else{
                log.error("image proxy error {}",imageResponse.getStatusCode());
                return ResponseEntity.status(imageResponse.getStatusCode()).body(null);
            }
        }
        return ResponseEntity.status(500).body(null);
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
