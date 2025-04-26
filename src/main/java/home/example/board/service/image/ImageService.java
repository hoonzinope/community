package home.example.board.service.image;

import home.example.board.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class ImageService {

    private final MinioConfig minioConfig;
    private final MinioClient minioClient;

    @Autowired
    public ImageService(MinioConfig minioConfig, MinioClient minioClient) {
        this.minioConfig = minioConfig;
        this.minioClient = minioClient;
    }

    public String uploadImage(MultipartFile imageFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
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
        minIOUrl = "http://imageStorage";
        String imageUrl = minIOUrl + "/" + bucketName + "/" + fileName;
        return imageUrl;
    }

    public void deleteImage(String imageUrl) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String bucketName = minioConfig.getBucketName();
        String minIOUrl = minioConfig.getMinioUrl();
        imageUrl = imageUrl.replace("http://imageStorage", minIOUrl);
        // 이미지 URL에서 파일명 추출 (예: http://172.30.1.86:9001/uploads/uuid.jpg)
        String basePath = minIOUrl + "/" + bucketName + "/";
        if(!imageUrl.startsWith(basePath)) {
            throw new IllegalArgumentException("올바른 이미지 URL이 아닙니다.");
        }
        String fileName = imageUrl.substring(basePath.length());

        // minIO에서 객체 삭제
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName).build());
    }
}
