package home.example.board.controller.api;

import home.example.board.config.MinioConfig;
import io.minio.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class ImageAPI {

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClient minioClient;

    @Operation(summary = "이미지 업로드", description = "이미지를 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "이미지 업로드 성공",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"url\" : \"image url\"}"
                                            )
                                    }
                            )
            }),
            @ApiResponse(
                    responseCode="400",
                    description = "이미지 업로드 실패",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 response",
                                                    value = "{\"error\" : \"error message\"}"
                                            )
                                    }
                            )
            })
    })
    @PostMapping("/api/image/upload")
    public ResponseEntity<JSONObject> addImage(
            @Parameter(
                    description = "업로드할 이미지 파일",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestParam("image")MultipartFile imageFile) {
        // 이미지 추가
        JSONObject response = new JSONObject();

        if(imageFile.isEmpty()){
            response.put("error", "no file upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 원본 파일명에서 확장자 추출
            String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String ext = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                ext = originalFilename.substring(dotIndex);
            }

            // UUID를 이용해 고유 파일명 생성
            String fileName = UUID.randomUUID().toString() + ext;
            String bucketName = minioConfig.getBucketName();
            String minIOUrl = minioConfig.getMinioUrl();

            // 버킷이 존재하는지 확인하고 없으면 생성
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 이미지 파일을 minIO에 업로드
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(imageFile.getInputStream(), imageFile.getSize(), -1)
                            .contentType(imageFile.getContentType())
                            .build()
            );

            // 저장된 파일에 접근 가능한 URL 구성 (버킷을 정적 리소스로 공개했다고 가정)
            String imageUrl = minIOUrl + "/" + bucketName + "/" + fileName;
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "이미지 업로드 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "이미지 삭제", description = "이미지를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "이미지 삭제 성공",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"message\" : \"이미지가 성공적으로 삭제되었습니다.\"}"
                                            )
                                    }
                            )
            }),
            @ApiResponse(
                    responseCode="400",
                    description = "이미지 삭제 실패",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 response",
                                                    value = "{\"error\" : \"error message\"}"
                                            )
                                    }
                            )
            })
    })
    @PostMapping("/api/image/delete")
    public ResponseEntity<JSONObject> deleteImage(
            @Parameter(
                    description = "삭제할 이미지 URL",
                    required = true,
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string"),
                            examples = {
                                    @ExampleObject(
                                            name = "이미지 URL",
                                            value = "image_url"
                                    )
                            }
                    )
            )
            @RequestParam("url") String imageUrl) {
        // 이미지 삭제
        JSONObject response = new JSONObject();

        if(imageUrl == null || imageUrl.trim().isEmpty()){
            response.put("error","유효하지 않은 이미지 URL");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String bucketName = minioConfig.getBucketName();
            String minIOUrl = minioConfig.getMinioUrl();
            // 이미지 URL에서 파일명 추출 (예: http://172.30.1.86:9001/uploads/uuid.jpg)
            String basePath = minIOUrl + "/" + bucketName + "/";
            if (!imageUrl.startsWith(basePath)) {
                response.put("error", "올바른 이미지 URL이 아닙니다.");
                return ResponseEntity.badRequest().body(response);
            }
            String fileName = imageUrl.substring(basePath.length());

            // minIO에서 객체 삭제
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
            response.put("message", "이미지가 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "이미지 삭제 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
