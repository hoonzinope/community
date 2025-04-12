package home.example.board.controller.api.comment;

import home.example.board.service.comment.ReadCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadCommentAPI {

    private final ReadCommentService readCommentService;
    @Autowired
    public ReadCommentAPI(ReadCommentService readCommentService) {
        this.readCommentService = readCommentService;
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
            @PathVariable long post_seq,
            @Parameter(description = "사용자 번호", required = false)
            @RequestParam(value="user_seq", defaultValue = "-1") long user_seq) {
        JSONObject jsonObject = readCommentService.selectComments(post_seq, user_seq);
        return ResponseEntity.ok().body(jsonObject);
    }
}
