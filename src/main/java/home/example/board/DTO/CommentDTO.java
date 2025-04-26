package home.example.board.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private long comment_seq;
    private String content;
    private long parent_comment_seq;
    private long reply_user_seq;
    private LocalDateTime insert_ts;
    private long user_seq;
    private String user_name;
    private String reply_to_user_name;
    private int like_cnt;
    private int dislike_cnt;
    private int depth;
    private LocalDateTime top_insert_ts;
//    private boolean click_like;
//    private int click_like_type;
}
