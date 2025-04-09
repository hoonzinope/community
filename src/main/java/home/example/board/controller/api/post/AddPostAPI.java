package home.example.board.controller.api.post;

import home.example.board.service.post.AddPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AddPostAPI {

    private final AddPostService addPostService;

    @Autowired
    public AddPostAPI(AddPostService addPostService) {
        this.addPostService = addPostService;
    }

    // 게시물 등록
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

        addPostService.addPost(title, content, user_seq, subject_seq);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", "true");
        return ResponseEntity.ok().body(jsonObject);
    }
}
