package home.example.board.controller.api.post;

import home.example.board.service.post.ReadPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ReadPostAPI {

    private final ReadPostService readPostService;

    @Autowired
    public ReadPostAPI(ReadPostService readPostService) {
        this.readPostService = readPostService;
    }

    // 게시물 목록 조회
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "게시글 목록 조회 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"postList\" : [{\"post_seq\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"user_seq\" : 1, \"subject_seq\" : 1, \"created_at\" : \"2021-01-01 00:00:00\"}], \"total\" : 1}"
                                            )
                                    }
                            )
                    })
    })
    @GetMapping("/api/posts")
    public ResponseEntity<JSONObject> getPosts(
        @Parameter(description = "페이징 offset", required = true)
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @Parameter(description = "페이징 limit", required = true)
        @RequestParam(value = "limit", defaultValue = "10") int limit,
        @Parameter(description = "주제", required = false)
        @RequestParam(value = "subject_seq", defaultValue = "0") long subject_seq
    ){
        log.info("offset: {}, limit: {}, subject_seq: {}", offset, limit, subject_seq);
        JSONObject postList = readPostService.getPostListPaging(offset, limit, subject_seq);
        return ResponseEntity.ok().body(postList);
    }

    // 게시글 조회
    @Operation(summary = "게시글 조회", description = "게시글을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "게시글 조회 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"post_seq\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"user_seq\" : 1, \"subject_seq\" : 1, \"created_at\" : \"2021-01-01 00:00:00\"}"
                                            )
                                    }
                            )
                    })
    })
    @GetMapping("/api/post/{post_seq}")
    public ResponseEntity<JSONObject> getPost(
            @Parameter(description = "게시글 번호", required = true)
            @PathVariable long post_seq
    ) {
        log.info("post_seq: {}", post_seq);
        JSONObject post = readPostService.getPost(post_seq);
        return ResponseEntity.ok().body(post);
    }

    // 유저 게시글 목록 조회
    @Operation(summary = "유저 게시글 목록 조회", description = "유저의 게시글 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "유저 게시글 목록 조회 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"postList\" : [{\"post_seq\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"user_seq\" : 1, \"subject_seq\" : 1, \"created_at\" : \"2021-01-01 00:00:00\"}], \"total\" : 1}"
                                            )
                                    }
                            )
                    })
    })
    @GetMapping("/api/post/user")
    public ResponseEntity<JSONObject> getUserPosts(
            @Parameter(description = "유저 번호", required = true)
            @RequestParam(value = "user_seq") long user_seq,
            @Parameter(description = "페이징 offset", required = true)
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @Parameter(description = "페이징 limit", required = true)
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        log.info("user_seq: {}, offset: {}, limit: {}", user_seq, offset, limit);
        JSONObject postList = readPostService.getUserPostListPaging(user_seq, offset, limit);
        return ResponseEntity.ok().body(postList);
    }

    // 본 게시물 목록 조회
    @Operation(summary = "본 게시물 목록 조회", description = "본 게시물 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "본 게시물 목록 조회 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"postList\" : [{\"post_seq\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"user_seq\" : 1, \"subject_seq\" : 1, \"created_at\" : \"2021-01-01 00:00:00\"}], \"total\" : 1}"
                                            )
                                    }
                            )
                    })
    })
    @PostMapping("/api/post/seen")
    public ResponseEntity<JSONObject> getSeenPosts(
            @RequestBody Map<String, Object> params
    ) {
        log.info("params: {}", params);
        List<Long> post_seq_list = (List<Long>) params.get("seenPostList");
        JSONObject postList = readPostService.getSeenPostListByPostSeqList(post_seq_list);
        return ResponseEntity.ok().body(postList);
    }
}
