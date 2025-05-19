package home.example.board.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Post {
    private long post_seq;
    private String title;
    private String content;
    private int view_count;
    private LocalDateTime insert_ts;
    private LocalDateTime update_ts;
    private boolean delete_flag;
    private LocalDateTime delete_ts;
    private long subject_seq;
    private long user_seq;
}
