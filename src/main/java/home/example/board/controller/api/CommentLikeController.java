package home.example.board.controller.api;

import home.example.board.service.CommentLikeService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CommentLikeController {

    @Autowired
    private CommentLikeService commentLikeService;

    @PostMapping("/api/comment/like")
    public ResponseEntity<JSONObject> insertCommentLike(@RequestBody  Map<String, Object> requestMap) {
        String like_type = (String) requestMap.get("like_type");
        int comment_seq = Integer.parseInt(requestMap.get("comment_seq").toString());
        int user_seq = Integer.parseInt(requestMap.get("user_seq").toString());
        try{
            if(!like_type.equalsIgnoreCase("like") && !like_type.equalsIgnoreCase("dislike")) {
                throw new Exception("like_type이 올바르지 않습니다.");
            }
            commentLikeService.deleteCommentLike(comment_seq, user_seq);
            commentLikeService.insertCommentLike(comment_seq, user_seq, like_type.toLowerCase());
            return ResponseEntity.ok().body(new JSONObject());
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new JSONObject());
        }
    }

    @PostMapping("/api/comment/like/delete")
    public ResponseEntity<JSONObject> deleteCommentLike(@RequestBody Map<String, Object> requestMap) {
        int comment_seq = Integer.parseInt(requestMap.get("comment_seq").toString());
        int user_seq = Integer.parseInt(requestMap.get("user_seq").toString());

        try{
            commentLikeService.deleteCommentLike(comment_seq, user_seq);
            return ResponseEntity.ok().body(new JSONObject());
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new JSONObject());
        }
    }

}
