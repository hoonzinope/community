package home.example.board.controller.api.bot;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.DTO.botApiDTO.BotAddPostDTO;
import home.example.board.service.bot.BotAddPostService;
import home.example.board.service.post.AddPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BotAddPostAPI {

    private final BotAddPostService botAddPostService;

    public BotAddPostAPI(BotAddPostService botAddPostService) {
        this.botAddPostService = botAddPostService;
    }

    // bot - 게시물 등록
    @Operation(summary = "bot 게시글 추가", description = "bot 게시글 추가 api")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "게시글 추가 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"success\" : \"true\"}"
                                            )
                                    }
                            )
                    })
    })
    @PostMapping("/api/bot/post")
    public ResponseEntity<JSONObject> addPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = JSONObject.class
                            ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "게시글 정보",
                                            value = "{\"title\" : \"title\", \"content\" : \"content\", \"subject_seq\" : \"subject_seq\"}"
                                    )
                            }
                    )
            )
            @RequestBody BotAddPostDTO botAddPostDTO) {
        log.info("게시글 정보: {}", botAddPostDTO);
        JSONObject response = new JSONObject();
        try {
            Long user_seq = this.getUserSeq();
            botAddPostService.addPostByBot(botAddPostDTO, user_seq);

            response.put("success", "true");
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            log.error("Error occurred while adding post: {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private Long getUserSeq() throws IllegalStateException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        return userDetail.getUserSeq();
    }
}
