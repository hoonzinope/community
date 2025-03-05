package home.example.board.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostLike {
    private long post_like_seq;
    private long post_seq;
    private long user_seq;
    private String like_type; // "LIKE" or "DISLIKE"
    private String insert_ts;
}
