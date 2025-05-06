package home.example.board.controller.api.postLike;

import home.example.board.domain.PostLike;
import home.example.board.service.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ReadPostLikeAPI {

    private final PostLikeService postLikeService;

    @Autowired
    public ReadPostLikeAPI(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
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
    @PostMapping("/api/like/get")
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
        log.info("getPostLike data: {}", data.toJSONString());
        long post_seq = Long.parseLong(data.get("post_seq").toString());
        long user_seq = Long.parseLong(data.get("user_seq").toString());

        JSONObject result = new JSONObject();
        try {
            PostLike postLike = postLikeService.getPostLike(post_seq, user_seq);
            result.put("result", "success");
            result.put("like_type", postLike != null ? postLike.getLike_type() : "");
            return ResponseEntity.ok(result);

        }catch (Exception e) {
            log.error("게시글 좋아요/싫어요 조회 실패: {}", e.getMessage());
            result.put("result", "fail");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Operation(summary = "유저별 게시글 좋아요 목록 조회", description = "게시글에 유저별 좋아요 목록을 조회합니다.")
    @ApiResponse(
            responseCode="200",
            description = "유저별 좋아요 목록 조회 성공",
            content = {
                    @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "정상 response",
                                            value = "{\"result\" : \"success\", \"postList\" : [{\"post_seq\" : 1, \"title\" : \"제목\", \"content\" : \"내용\", \"user_seq\" : 1, \"subject_seq\" : 1, \"insert_ts\" : \"2023-01-01T00:00:00Z\"}]}"
                                    )
                            }
                    )
            })
    @GetMapping("/api/like/user")
    public ResponseEntity<JSONObject> getPostLikeUsers(
            @RequestParam(value = "user_seq", required = true) long user_seq,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        log.info("게시글 좋아요 목록 조회 API 호출 : user_seq = {}, offset = {}, limit = {}", user_seq, offset, limit);
        try {
            JSONObject posts = postLikeService.getLikePostsByUser(user_seq, offset, limit);
            return ResponseEntity.ok().body(posts);
        }catch (Exception e) {
            log.error("게시글 좋아요 목록 조회 실패 : {}", e.getMessage());
            JSONObject result = new JSONObject();
            result.put("result", "fail");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Operation(summary = "유저별 게시글 싫어요 목록 조회", description = "게시글에 유저별 싫어요 목록을 조회합니다.")
    @ApiResponse(
            responseCode="200",
            description = "유저별 싫어요 목록 조회 성공",
            content = {
                    @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "정상 response",
                                            value = "{\"result\" : \"success\", \"postList\" : [{\"post_seq\" : 1, \"title\" : \"제목\", \"content\" : \"내용\", \"user_seq\" : 1, \"subject_seq\" : 1, \"insert_ts\" : \"2023-01-01T00:00:00Z\"}]}"
                                    )
                            }
                    )
            })
    @GetMapping("/api/dislike/user")
    public ResponseEntity<JSONObject> getDisLikePosts(
            @RequestParam(value = "user_seq", required = true) long user_seq,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        log.info("게시글 싫어요 목록 조회 API 호출 : user_seq = {}, offset = {}, limit = {}", user_seq, offset, limit);
        try {
            JSONObject posts = postLikeService.getDisLikePostsByUser(user_seq, offset, limit);
            return ResponseEntity.ok().body(posts);
        }catch (Exception e) {
            log.error("게시글 싫어요 목록 조회 실패 : {}", e.getMessage());
            JSONObject result = new JSONObject();
            result.put("result", "fail");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
