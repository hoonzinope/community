package home.example.board.DTO.botApiDTO;

import lombok.Data;

@Data
public class BotAddCommentDTO {
    Long post_seq;
    String content;
    Long parent_comment_seq;
    Long reply_user_seq;
}
