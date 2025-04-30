package home.example.board.controller.api.bot;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.DTO.botApiDTO.BotAddCommentDTO;
import home.example.board.service.bot.BotAddCommentService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotAddCommentAPI {

    private final BotAddCommentService botAddCommentService;

    @Autowired
    public BotAddCommentAPI(BotAddCommentService botAddCommentService) {
        this.botAddCommentService = botAddCommentService;
    }

    @PostMapping("/api/bot/comment")
    public ResponseEntity<JSONObject> addComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = JSONObject.class
                            ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "댓글 정보",
                                            value = "{\"post_seq\" : 1, \"content\" : \"content\", \"parent_comment_seq\" : 1, \"reply_user_seq\" : 1}"
                                    )
                            }
                    )
            )
            @RequestBody BotAddCommentDTO botAddCommentDTO)
    {
        JSONObject response = new JSONObject();
        try {
            long user_seq = getUserSeq();
            botAddCommentService.addCommentByBot(botAddCommentDTO, user_seq);
            response.put("success", "true");
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            response.put("success", "false");
            response.put("message", e.getMessage());
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
