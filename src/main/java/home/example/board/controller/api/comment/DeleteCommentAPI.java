package home.example.board.controller.api.comment;

import home.example.board.service.comment.RemoveCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DeleteCommentAPI {

    private final RemoveCommentService removeCommentService;
    @Autowired
    public DeleteCommentAPI(RemoveCommentService removeCommentService) {
        this.removeCommentService = removeCommentService;
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제 처리합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "댓글 삭제 처리 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"success\" : true}"
                                            )
                                    }
                            )
                    }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="400",
                    description = "댓글 삭제 처리 실패",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "에러 response",
                                                    value = "{\"message\" : \"error message\"}"
                                            )
                                    }
                            )
                    })
    })
    @DeleteMapping("/api/comment/delete/{comment_seq}")
    public ResponseEntity<JSONObject> deleteComment(
            @Parameter(description = "댓글 번호", required = true)
            @PathVariable long comment_seq,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "유저 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    type = "object",
                                    implementation = Map.class
                            ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "유저 정보",
                                            value = "{\"user_seq\" : 1}"
                                    )
                            }
                    )
            )
            @RequestBody Map<String, Object> requestBody
    ){
        long user_seq = Long.parseLong(requestBody.get("user_seq").toString());
        JSONObject jsonObject = new JSONObject();
        try{
            removeCommentService.deleteComment(comment_seq, user_seq);
            jsonObject.put("success", true);
            return ResponseEntity.ok().body(jsonObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            jsonObject.put("message", e.getMessage());
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
    }
}
