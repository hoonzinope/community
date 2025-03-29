package home.example.board.dao;

import home.example.board.domain.Comment;
import home.example.board.domain.Outbox;
import home.example.board.domain.Post;
import home.example.board.repository.OutboxMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OutboxDAO {

    @Autowired
    private OutboxMapper outboxMapper;

    public void insertOutbox(Post post, String eventType) {
        // convert Post to Outbox
        Outbox outbox = Outbox.builder()
                .aggregate_type("POST")
                .aggregate_id(post.getPost_seq())
                .event_type(eventType)
                .payload(postToJson(post).toJSONString())
                .created_ts(LocalDateTime.now())
                .status("PENDING")
                .build();
        // insert Outbox into database
        outboxMapper.insertOutbox(outbox);
    }

    private JSONObject postToJson(Post post) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("post_seq", post.getPost_seq());
        jsonObject.put("title", post.getTitle());
        jsonObject.put("content", post.getContent());
        return jsonObject;
    }

    public void insertComment(Comment comment, String eventType) {
        // convert Comment to Outbox
        Outbox outbox = Outbox.builder()
                .aggregate_type("COMMENT")
                .aggregate_id(comment.getComment_seq())
                .event_type(eventType)
                .payload(commentToJson(comment).toJSONString())
                .created_ts(LocalDateTime.now())
                .status("PENDING")
                .build();
        // insert Outbox into database
        outboxMapper.insertOutbox(outbox);
    }

    private JSONObject commentToJson(Comment comment) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comment_seq", comment.getComment_seq());
        jsonObject.put("post_seq", comment.getPost_seq());
        jsonObject.put("content", comment.getContent());
        return jsonObject;
    }
}
