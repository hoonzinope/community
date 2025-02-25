package home.example.board.controller.api;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.json.simple.JSONObject;
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

    private final String minIOUrl = "http://172.30.1.86:9000";
    private final String accessKey = "minioadmin";
    private final String secretKey = "minioadmin";
    private final String bucketName = "images";

    private final MinioClient minioClient;
    public ImageAPI() {
        this.minioClient = MinioClient.builder()
                .endpoint(minIOUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    @PostMapping("/api/image/upload")
    public ResponseEntity<JSONObject> addImage(@RequestParam("image")MultipartFile imageFile) {
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

    @PostMapping("/api/image/delete")
    public void deleteImage() {
        // 이미지 삭제
    }


}
