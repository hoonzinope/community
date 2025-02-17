package home.example.board.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Comment {
    private int comment_seq;
    private Long post_seq;
    private String content;
    private LocalDateTime insert_ts;
    private LocalDateTime delete_ts;
    private Integer delete_flag;
    private Integer parent_comment_seq;
    private Long user_seq;
}
