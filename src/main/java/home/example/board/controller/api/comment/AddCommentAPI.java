package home.example.board.controller.api.comment;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.service.comment.AddCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class AddCommentAPI {

    private final AddCommentService addCommentService;
    @Autowired
    public AddCommentAPI(AddCommentService addCommentService) {
        this.addCommentService = addCommentService;
    }

    @Operation(summary = "댓글 추가", description = "댓글을 추가합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "댓글 추가 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"result\" : \"success\"}"
                                            )
                                    }
                            )
                    }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="400",
                    description = "댓글 추가 실패",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "에러 response",
                                                    value = "{\"result\" : \"fail\", \"message\" : \"error message\"}"
                                            )
                                    }
                            )
                    })
    })
    @PostMapping("/api/post/{post_seq}/comment")
    public ResponseEntity<JSONObject> addComment(
            @Parameter(description = "게시글 번호", required = true)
            @PathVariable long post_seq,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    type = "object",
                                    implementation = Map.class
                            ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "댓글 정보",
                                            description = "댓글 내용, 부모 댓글 번호, 사용자 번호",
                                            summary = "댓글 정보",
                                            value = "{\"content\" : \"댓글 내용\", \"parent_comment_seq\" : 1, \"reply_user_seq\" : 1}"
                                    )
                            }
                    )
            )
            @RequestBody Map<String, Object> requestBody, @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("add comment: {}", requestBody);
        String content = (String) requestBody.get("content");
        Long parent_comment_seq =
                requestBody.get("parent_comment_seq") == null ?
                        null : Long.parseLong(requestBody.get("parent_comment_seq").toString());
        Long reply_user_seq =
                requestBody.get("reply_user_seq") == null ?
                        null : Long.parseLong(requestBody.get("reply_user_seq").toString());

        Long user_seq = userDetail != null ? userDetail.getUserSeq() : null;//Long.parseLong(requestBody.get("user_seq").toString());
        JSONObject jsonObject = new JSONObject();
        try{
            if(user_seq == null){
                log.error("user_seq is null");
                jsonObject.put("message", "로그인 후 이용해주세요.");
                return ResponseEntity.badRequest().body(jsonObject);
            }
            if(content == null || content.isEmpty()){
                log.error("content is null");
                jsonObject.put("message", "댓글 내용을 입력해주세요.");
                return ResponseEntity.badRequest().body(jsonObject);
            }
            addCommentService.addComment(post_seq, content, parent_comment_seq, reply_user_seq, user_seq);
            jsonObject.put("message", "댓글이 등록되었습니다.");
            return ResponseEntity.ok().body(jsonObject);
        } catch (Exception e){
            log.error("add comment error: {}", e.getMessage());
            jsonObject.put("message", "댓글 등록에 실패했습니다.");
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
    }

}
