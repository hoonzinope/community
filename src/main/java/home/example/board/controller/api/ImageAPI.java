package home.example.board.controller.api;

import home.example.board.config.MinioConfig;
import home.example.board.service.image.ImageService;
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

    private final ImageService imageService;

    @Autowired
    public ImageAPI(ImageService imageService) {
        this.imageService = imageService;
    }

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
            String imageUrl = imageService.uploadImage(imageFile);
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
            imageService.deleteImage(imageUrl);
            response.put("message", "이미지가 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "이미지 삭제 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
