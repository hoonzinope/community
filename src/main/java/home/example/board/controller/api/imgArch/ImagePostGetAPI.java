package home.example.board.controller.api.imgArch;

import home.example.board.DTO.imageArch.ImagePostPagingResponseDTO;
import home.example.board.DTO.imageArch.ImagePostRequestDTO;
import home.example.board.service.imageArch.ImageArchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ImagePostGetAPI {

    private final ImageArchService imageArchService;
    @Autowired
    public ImagePostGetAPI(ImageArchService imageArchService) {
        this.imageArchService = imageArchService;
    }

    // get image post with pagination
    @Operation( summary = "이미지 게시글 페이징 조회", description = "요청된 페이지 매개변수를 기반으로 이미지 게시글 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "이미지 게시글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "ImagePostPagingResponseDTO",
                                    value = "{ \"limit\": 10, \"offset\": 0, \"totalCount\": 100, \"imagePostList\": [] }"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류"
            )
    })
    @PostMapping("/api/image-posts")
    public ResponseEntity<ImagePostPagingResponseDTO> getImagePostList(
            @RequestBody ImagePostRequestDTO imagePostRequestDTO) {
        // Implement the logic to get image posts with pagination
        log.info("getImagePostList called with request: {}", imagePostRequestDTO);
        try{
            ImagePostPagingResponseDTO imagePostList = imageArchService.getImagePostList(imagePostRequestDTO);
            return ResponseEntity.ok().body(imagePostList);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Error occurred while fetching image posts: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
