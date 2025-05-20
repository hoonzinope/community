package home.example.board.dao;

import home.example.board.domain.Comment;
import home.example.board.domain.Outbox;
import home.example.board.domain.Post;
import home.example.board.repository.OutboxMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Repository
public class OutboxDAO {

    @Autowired
    private OutboxMapper outboxMapper;

    public void insertPost(long post_seq, String title, String content, String eventType) {
        JSONObject payload = postToJson(post_seq, title, content);
        // convert Post to Outbox
        Outbox outbox = Outbox.builder()
                .aggregate_type("POST")
                .aggregate_id(post_seq)
                .event_type(eventType)
                .payload(payload.toJSONString())
                .created_ts(LocalDateTime.now())
                .status("PENDING")
                .build();
        // insert Outbox into database
        outboxMapper.insertOutbox(outbox);
    }

    private JSONObject postToJson(long post_seq, String title, String content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("post_seq", post_seq);
        jsonObject.put("title", title);
        jsonObject.put("content", content);
        return jsonObject;
    }

    public void insertComment(long comment_seq, String content, String eventType) {
        // convert Comment to JSON
        JSONObject payload = commentToJson(comment_seq, content);

        // convert Comment to Outbox
        Outbox outbox = Outbox.builder()
                .aggregate_type("COMMENT")
                .aggregate_id(comment_seq)
                .event_type(eventType)
                .payload(payload.toJSONString())
                .created_ts(LocalDateTime.now())
                .status("PENDING")
                .build();
        // insert Outbox into database
        outboxMapper.insertOutbox(outbox);
    }

    private JSONObject commentToJson(long comment_seq, String content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comment_seq", comment_seq);
        jsonObject.put("content", content);
        return jsonObject;
    }

    public void removePostAllByUser(long user_seq) {
        // convert Post to Outbox
        Outbox outbox = Outbox.builder()
                .aggregate_type("POST")
                .aggregate_id(user_seq)
                .event_type("UserSoftDeleted")
                .payload(new JSONObject().toJSONString())
                .created_ts(LocalDateTime.now())
                .status("PENDING")
                .build();
        // insert Outbox into database
        outboxMapper.insertOutbox(outbox);
    }

    public void restorePostAllByUser(long user_seq) {
        // convert Post to Outbox
        Outbox outbox = Outbox.builder()
                .aggregate_type("POST")
                .aggregate_id(user_seq)
                .event_type("UserRestore")
                .payload(new JSONObject().toJSONString())
                .created_ts(LocalDateTime.now())
                .status("PENDING")
                .build();
        // insert Outbox into database
        outboxMapper.insertOutbox(outbox);
    }

    public void removeCommentAllByUser(long user_seq) {
        // convert Comment to Outbox
        Outbox outbox = Outbox.builder()
                .aggregate_type("COMMENT")
                .aggregate_id(user_seq)
                .event_type("UserSoftDeleted")
                .payload(new JSONObject().toJSONString())
                .created_ts(LocalDateTime.now())
                .status("PENDING")
                .build();
        // insert Outbox into database
        outboxMapper.insertOutbox(outbox);
    }

    public void restoreCommentAllByUser(long user_seq) {
        // convert Comment to Outbox
        Outbox outbox = Outbox.builder()
                .aggregate_type("COMMENT")
                .aggregate_id(user_seq)
                .event_type("UserRestore")
                .payload(new JSONObject().toJSONString())
                .created_ts(LocalDateTime.now())
                .status("PENDING")
                .build();
        // insert Outbox into database
        outboxMapper.insertOutbox(outbox);
    }
}
