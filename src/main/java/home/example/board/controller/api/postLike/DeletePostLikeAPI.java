package home.example.board.controller.api.postLike;

import home.example.board.service.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DeletePostLikeAPI {

    private final PostLikeService postLikeService;

    @Autowired
    public DeletePostLikeAPI(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
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
        log.info("게시글 좋아요/싫어요 삭제 API 호출 : {}", data.toJSONString());
        long post_seq = Long.parseLong(data.get("post_seq").toString());
        long user_seq = Long.parseLong(data.get("user_seq").toString());

        try{
            postLikeService.removePostLike(post_seq, user_seq);
            JSONObject result = new JSONObject();
            result.put("result", "success");
            return ResponseEntity.ok(result);
        }catch (Exception e) {
            log.error("게시글 좋아요/싫어요 삭제 실패 : {}", e.getMessage());
            JSONObject result = new JSONObject();
            result.put("result", "fail");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
