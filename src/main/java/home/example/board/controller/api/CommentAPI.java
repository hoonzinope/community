package home.example.board.controller.api;

import home.example.board.domain.CommentLike;
import home.example.board.service.CommentLikeService;
import home.example.board.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CommentAPI {
    @Autowired
    private CommentService commentService;

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
                                            value = "{\"content\" : \"댓글 내용\", \"parent_comment_seq\" : 1, \"user_seq\" : 1}"
                                    )
                            }
                    )
            )
            @RequestBody Map<String, Object> requestBody) {
        String content = (String) requestBody.get("content");
        Long parent_comment_seq =
                requestBody.get("parent_comment_seq") == null ?
                        null : Long.parseLong(requestBody.get("parent_comment_seq").toString());
        long user_seq = Long.parseLong(requestBody.get("user_seq").toString());
        JSONObject jsonObject = new JSONObject();
        try{
            if(content == null || content.isEmpty()){
                jsonObject.put("message", "댓글 내용을 입력해주세요.");
                return ResponseEntity.badRequest().body(jsonObject);
            }
            commentService.insertComment(post_seq, content, parent_comment_seq, user_seq);
            jsonObject.put("message", "댓글이 등록되었습니다.");
            return ResponseEntity.ok().body(jsonObject);
        } catch (Exception e){
            e.printStackTrace();
            jsonObject.put("message", "댓글 등록에 실패했습니다.");
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
    }

    @Operation(summary = "게시물 댓글 가져오기", description = "게시물의 달린 댓글을 가져옵니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "댓글들 조회 성공",
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
                    description = "댓글들 조회 실패",
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
    @GetMapping("/api/post/{post_seq}/comments")
    public ResponseEntity<JSONObject> getComments(
            @Parameter(description = "게시글 번호", required = true)
            @PathVariable int post_seq,
            @Parameter(description = "사용자 번호", required = false)
            @RequestParam(value="user_seq", defaultValue = "-1") long user_seq) {
        JSONObject jsonObject = commentService.selectComments(post_seq, user_seq);
        return ResponseEntity.ok().body(jsonObject);
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
            commentService.updateComment(comment_seq, content, user_seq);
            jsonObject.put("success", true);
            return ResponseEntity.ok().body(jsonObject);
        } catch (Exception e){
            e.printStackTrace();
            jsonObject.put("message", e.getMessage());
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
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
            commentService.deleteComment(comment_seq, user_seq);
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
