package home.example.board.controller.api.comment;

import home.example.board.service.comment.ModifyCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ModifyAPI {

    private final ModifyCommentService modifyCommentService;
    @Autowired
    public ModifyAPI(ModifyCommentService modifyCommentService) {
        this.modifyCommentService = modifyCommentService;
    }

    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "댓글 수정 성공",
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
                    description = "댓글 수정 실패",
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
    @PostMapping("/api/comment/update/{comment_seq}")
    public ResponseEntity<JSONObject> updateComment(
            @Parameter(description = "댓글 번호", required = true)
            @PathVariable long comment_seq,
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
                                            value = "{\"content\" : \"댓글 내용\", \"user_seq\" : 1}"
                                    )
                            }
                    )
            )
            @RequestBody Map<String, Object> requestBody
    ){
        String content = (String) requestBody.get("content");
        long user_seq = Long.parseLong(requestBody.get("user_seq").toString());

        JSONObject jsonObject = new JSONObject();
        try{
            if(content == null || content.isEmpty()){
                throw new IllegalArgumentException("댓글내용이 비어 있습니다.");
            }
            modifyCommentService.modifyComment(comment_seq, content, user_seq);
            jsonObject.put("success", true);
            return ResponseEntity.ok().body(jsonObject);
        } catch (Exception e){
            e.printStackTrace();
            jsonObject.put("message", e.getMessage());
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
    }
}
