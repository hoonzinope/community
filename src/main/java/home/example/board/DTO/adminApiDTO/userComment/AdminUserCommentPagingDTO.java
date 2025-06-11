package home.example.board.DTO.adminApiDTO.userComment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserCommentPagingDTO {
    private long comment_seq;
    private String post_title;
    private String content;
    private LocalDateTime insert_ts;
}
