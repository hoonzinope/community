package home.example.board.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentLike {
    private long comment_like_seq;
    private long comment_seq;
    private long user_seq;
    private int like_type;
    private LocalDateTime insert_ts;
}
