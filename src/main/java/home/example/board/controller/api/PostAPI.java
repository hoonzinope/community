package home.example.board.controller.api;

import home.example.board.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PostAPI {

    @Autowired
    PostService postService;

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
            @Parameter(description = "페이징 offset", required = true) @RequestParam(value = "offset", defaultValue = "0") int offset,
            @Parameter(description = "페이징 limit", required = true) @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        JSONObject postListPaging = postService.getPostListPaging(offset, limit);
        return ResponseEntity.ok().body(postListPaging);
    }

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
            @Parameter(description = "게시글 번호", required = true) @PathVariable long post_seq
    ) {
        JSONObject post = postService.getPost(post_seq);
        return ResponseEntity.ok().body(post);
    }

    @Operation(summary = "게시글 추가", description = "게시글을 추가합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "게시글 추가 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"success\" : \"true\"}"
                                            )
                                    }
                            )
            })
    })
    @PostMapping("/api/post")
    public ResponseEntity<JSONObject> addPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = JSONObject.class
                            ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "게시글 정보",
                                            value = "{\"title\" : \"title\", \"content\" : \"content\", \"user_seq\" : 1, \"subject_seq\" : 1}"
                                    )
                            }
                    )
            )
            @RequestBody Map<String, Object> requestMap
    ) {
        String title = (String) requestMap.get("title");
        String content = (String) requestMap.get("content");
        long user_seq = Long.parseLong(requestMap.get("user_seq").toString());
        int subject_seq = Integer.parseInt(requestMap.get("subject_seq").toString());

        //System.out.println("title: " + title+ ", content: " + content + ", user_seq: " + user_seq + ", subject_seq: " + subject_seq);
        postService.addPost(title, content, user_seq, subject_seq);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", "true");
        return ResponseEntity.ok().body(jsonObject);
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "게시글 수정 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"success\" : \"true\"}"
                                            )
                                    }
                            )
            }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="400",
                    description = "게시글 수정 실패",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "에러 response",
                                                    value = "{\"error\" : \"error message\"}"
                                            )
                                    }
                            )
            })
    })
    @PatchMapping("/api/post/{post_seq}")
    public ResponseEntity<JSONObject> modifyPost(
            @Parameter(description = "게시글 번호", required = true)
            @PathVariable long post_seq,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = JSONObject.class
                            ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "게시글 정보",
                                            value = "{\"title\" : \"title\", \"content\" : \"content\"}"
                                    )
                            }
                    )
            )
            @RequestBody Map<String, Object> requestMap
    ) {
        String title = (String) requestMap.get("title");
        String content = (String) requestMap.get("content");

        JSONObject jsonObject = new JSONObject();
        try{
            postService.modifyPost(post_seq, title, content);
            jsonObject.put("success","true");
            return ResponseEntity.ok().body(jsonObject);
        } catch (IllegalArgumentException e) {
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="200",
                    description = "게시글 삭제 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"success\" : \"true\"}"
                                            )
                                    }
                            )
            }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode="400",
                    description = "게시글 삭제 실패",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "에러 response",
                                                    value = "{\"error\" : \"error message\"}"
                                            )
                                    }
                            )
            })
    })
    @DeleteMapping("/api/post/{post_seq}")
    public ResponseEntity<JSONObject> removePost(
            @Parameter(description = "게시글 번호", required = true)
            @PathVariable long post_seq
    ) {

        JSONObject jsonObject = new JSONObject();
        try{
            postService.removePost(post_seq);
            jsonObject.put("success", "true");
            return ResponseEntity.ok().body(jsonObject);
        } catch (IllegalArgumentException e) {
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
    }
}
