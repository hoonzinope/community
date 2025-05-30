package home.example.board.controller.api.imgArch;

import home.example.board.config.MinioConfig;
import home.example.board.config.RedisConfig;
import home.example.board.dao.imageArch.ImageFetchDAO;
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

    private final ImageFetchDAO imageFetchDAO;

    @Autowired
    public ImageProxyAPI(ImageFetchDAO imageFetchDAO) {
        this.imageFetchDAO = imageFetchDAO;
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
        return imageFetchDAO.getImage(imageEncodedUrl);
    }
}
