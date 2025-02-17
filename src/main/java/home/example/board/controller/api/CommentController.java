package home.example.board.controller.api;

import home.example.board.service.CommentService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/api/post/{post_seq}/comment")
    public ResponseEntity<JSONObject> addComment(
            @PathVariable long post_seq,
            @RequestBody Map<String, Object> requestBody) {
        String content = (String) requestBody.get("content");
        Integer parent_comment_seq =
                requestBody.get("parent_comment_seq") == null ?
                        null : Integer.parseInt(requestBody.get("parent_comment_seq").toString());
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

    @GetMapping("/api/post/{post_seq}/comments")
    public ResponseEntity<JSONObject> getComments(
            @PathVariable int post_seq) {

        JSONObject jsonObject = commentService.selectComments(post_seq);
        return ResponseEntity.ok().body(jsonObject);
    }
}
