package home.example.board.controller.api;

import home.example.board.service.CommentLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CommentLikeAPI {

    @Autowired
    private CommentLikeService commentLikeService;

    @Operation(summary = "댓글 좋아요/싫어요 추가", description = "댓글에 좋아요 또는 싫어요를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "좋아요/싫어요 추가 성공",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"result\" : \"success\"}"
                                            )
                                    }
                            )
            }),
            @ApiResponse(
                    responseCode="400",
                    description = "좋아요/싫어요 추가 실패",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 response",
                                                    value = "{\"result\" : \"fail\", \"message\" : \"error message\"}"
                                            )
                                    }
                            )
            })
    })
    @PostMapping("/api/comment/like")
    public ResponseEntity<JSONObject> insertCommentLike(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 좋아요/싫어요 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = Map.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "좋아요 추가",
                                            value = "{\"like_type\" : \"like\", \"comment_seq\" : 1, \"user_seq\" : 1}"
                                    ),
                                    @ExampleObject(
                                            name = "싫어요 추가",
                                            value = "{\"like_type\" : \"dislike\", \"comment_seq\" : 1, \"user_seq\" : 1}"
                                    )
                            }
                    )
            )
            @RequestBody  Map<String, Object> requestMap) {
        String like_type = (String) requestMap.get("like_type");
        long comment_seq = Long.parseLong(requestMap.get("comment_seq").toString());
        long user_seq = Long.parseLong(requestMap.get("user_seq").toString());
        try{
            if(!like_type.equalsIgnoreCase("like") && !like_type.equalsIgnoreCase("dislike")) {
                throw new Exception("like_type이 올바르지 않습니다.");
            }
            commentLikeService.deleteCommentLike(comment_seq, user_seq);
            commentLikeService.insertCommentLike(comment_seq, user_seq, like_type.toLowerCase());
            return ResponseEntity.ok().body(new JSONObject());
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new JSONObject());
        }
    }

    @Operation(summary = "댓글 좋아요/싫어요 삭제", description = "댓글에 좋아요 또는 싫어요를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "좋아요/싫어요 삭제 성공",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"result\" : \"success\"}"
                                            )
                                    }
                            )
            }),
            @ApiResponse(
                    responseCode="400",
                    description = "좋아요/싫어요 삭제 실패",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 response",
                                                    value = "{\"result\" : \"fail\", \"message\" : \"error message\"}"
                                            )
                                    }
                            )
            })
    })
    @PostMapping("/api/comment/like/delete")
    public ResponseEntity<JSONObject> deleteCommentLike(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 좋아요/싫어요 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = Map.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "좋아요 삭제",
                                            value = "{\"comment_seq\" : 1, \"user_seq\" : 1}"
                                    ),
                                    @ExampleObject(
                                            name = "싫어요 삭제",
                                            value = "{\"comment_seq\" : 1, \"user_seq\" : 1}"
                                    )
                            }
                    )
            )
            @RequestBody Map<String, Object> requestMap) {
        long comment_seq = Integer.parseInt(requestMap.get("comment_seq").toString());
        long user_seq = Integer.parseInt(requestMap.get("user_seq").toString());

        try{
            commentLikeService.deleteCommentLike(comment_seq, user_seq);
            return ResponseEntity.ok().body(new JSONObject());
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new JSONObject());
        }
    }

}
