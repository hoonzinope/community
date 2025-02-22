package home.example.board.controller.api;

import home.example.board.domain.PostLike;
import home.example.board.service.PostLikeService;
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
    @PostMapping("/like/add")
    public ResponseEntity<JSONObject> addPostLike(@RequestBody JSONObject data) {
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
    @PostMapping("/like/delete")
    public ResponseEntity<JSONObject> removePostLike(@RequestBody JSONObject data) {
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

    @PostMapping("/like/get")
    public ResponseEntity<JSONObject> getPostLike(@RequestBody JSONObject data) {
        long post_seq = Long.parseLong(data.get("post_seq").toString());
        long user_seq = Long.parseLong(data.get("user_seq").toString());

        JSONObject result = new JSONObject();
        try {
            PostLike postLike = postLikeService.getPostLike(post_seq, user_seq);
            result.put("result", "success");
            result.put("like_type", postLike.getLike_type());
            return ResponseEntity.ok(result);

        }catch (Exception e) {
            e.printStackTrace();
            result.put("result", "fail");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
