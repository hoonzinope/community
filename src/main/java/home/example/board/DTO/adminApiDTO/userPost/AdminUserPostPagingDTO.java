package home.example.board.DTO.adminApiDTO.userPost;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserPostPagingDTO {
    private long post_seq;
    private String title;
    private String content;
    private LocalDateTime insert_ts;
    private LocalDateTime update_ts;
    private int view_count;
    private long user_seq;
    private String user_nickname;
    private String category;
    private long subject_seq;
    private int like_count;
    private int dislike_count;
    private int comment_count;
    private boolean delete_flag;
    private LocalDateTime delete_ts;
}
