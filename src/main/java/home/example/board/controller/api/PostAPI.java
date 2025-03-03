package home.example.board.controller.api;

import home.example.board.service.PostService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PostAPI {

    @Autowired
    PostService postService;

    @GetMapping("/api/posts")
    public ResponseEntity<JSONObject> getPosts(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        JSONObject postListPaging = postService.getPostListPaging(offset, limit);
        return ResponseEntity.ok().body(postListPaging);
    }

    @GetMapping("/api/post/{post_seq}")
    public ResponseEntity<JSONObject> getPost(
            @PathVariable long post_seq
    ) {
        JSONObject post = postService.getPost(post_seq);
        return ResponseEntity.ok().body(post);
    }

    @PostMapping("/api/post")
    public ResponseEntity<JSONObject> addPost(
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

    @PatchMapping("/api/post/{post_seq}")
    public ResponseEntity<JSONObject> modifyPost(
            @PathVariable long post_seq,
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

    @DeleteMapping("/api/post/{post_seq}")
    public ResponseEntity<JSONObject> removePost(
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
