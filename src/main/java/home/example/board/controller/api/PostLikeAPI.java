package home.example.board.controller.api;

import home.example.board.domain.PostLike;
import home.example.board.service.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostLikeAPI {

    @Autowired
    PostLikeService postLikeService;

    // 좋아요 or 싫어요 추가
    @Operation(summary = "게시글 좋아요/싫어요 추가", description = "게시글에 좋아요 또는 싫어요를 추가합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "좋아요/싫어요 추가 성공",
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
                    description = "좋아요/싫어요 추가 실패",
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
    @PostMapping("/api/like/add")
    public ResponseEntity<JSONObject> addPostLike(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 좋아요/싫어요 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = JSONObject.class
                            ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "좋아요/싫어요 정보",
                                            value = "{\"post_seq\" : 1, \"user_seq\" : 1, \"like_type\" : \"like\"}"
                                    )
                            }
                    )
            )
            @RequestBody JSONObject data) {
        long post_seq = Long.parseLong(data.get("post_seq").toString());
        long user_seq = Long.parseLong(data.get("user_seq").toString());
        String like_type = (String) data.get("like_type");

        JSONObject result = new JSONObject();
        try {
            postLikeService.removePostLike(post_seq, user_seq);
            postLikeService.addPostLike(post_seq, user_seq, like_type);
            result.put("result", "success");
            return ResponseEntity.ok(result);
        }catch (Exception e) {
            e.printStackTrace();
            result.put("result", "fail");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // postLike table row 삭제
    @Operation(summary = "게시글 좋아요/싫어요 삭제", description = "게시글에 좋아요 또는 싫어요를 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "좋아요/싫어요 삭제 성공",
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
                    description = "좋아요/싫어요 삭제 실패",
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
    @PostMapping("/api/like/delete")
    public ResponseEntity<JSONObject> removePostLike(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 좋아요/싫어요 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = JSONObject.class
                            ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "좋아요/싫어요 정보",
                                            value = "{\"post_seq\" : 1, \"user_seq\" : 1}"
                                    )
                            }
                    )
            )
            @RequestBody JSONObject data) {
        long post_seq = Long.parseLong(data.get("post_seq").toString());
        long user_seq = Long.parseLong(data.get("user_seq").toString());

        try{
            postLikeService.removePostLike(post_seq, user_seq);
            JSONObject result = new JSONObject();
            result.put("result", "success");
            return ResponseEntity.ok(result);
        }catch (Exception e) {
            e.printStackTrace();
            JSONObject result = new JSONObject();
            result.put("result", "fail");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Operation(summary = "유저별 게시글 좋아요/싫어요 조회", description = "게시글에 유저별 좋아요 또는 싫어요를 조회합니다.")
    @ApiResponse(
            responseCode="200",
            description = "유저별 좋아요/싫어요 조회 성공",
            content = {
                    @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "정상 response",
                                            value = "{\"result\" : \"success\", \"like_type\" : \"like\"}"
                                    )
                            }
                    )
    })
    @PostMapping("/like/get")
    public ResponseEntity<JSONObject> getPostLike(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "유저별 게시글 좋아요/싫어요 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = JSONObject.class
                            ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "좋아요/싫어요 정보",
                                            value = "{\"post_seq\" : 1, \"user_seq\" : 1}"
                                    )
                            }
                    )
            )
            @RequestBody JSONObject data) {
        long post_seq = Long.parseLong(data.get("post_seq").toString());
        long user_seq = Long.parseLong(data.get("user_seq").toString());

        JSONObject result = new JSONObject();
        try {
            PostLike postLike = postLikeService.getPostLike(post_seq, user_seq);
            result.put("result", "success");
            result.put("like_type", postLike != null ? postLike.getLike_type() : "");
            return ResponseEntity.ok(result);

        }catch (Exception e) {
            e.printStackTrace();
            result.put("result", "fail");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
