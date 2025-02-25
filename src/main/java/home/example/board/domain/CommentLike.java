package home.example.board.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentLike {
    private int comment_like_seq;
    private int comment_seq;
    private int user_seq;
    private int like_type;
    private LocalDateTime insert_ts;
}
