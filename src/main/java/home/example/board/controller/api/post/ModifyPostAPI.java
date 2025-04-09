package home.example.board.controller.api.post;

import home.example.board.service.post.ModifyPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ModifyPostAPI {

    private final ModifyPostService modifyPostService;

    @Autowired
    public ModifyPostAPI(ModifyPostService modifyPostService) {
        this.modifyPostService = modifyPostService;
    }

    // 게시물 수정
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
            modifyPostService.modifyPost(post_seq, title, content);
            jsonObject.put("success","true");
            return ResponseEntity.ok().body(jsonObject);
        } catch (IllegalArgumentException e) {
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
    }
}
