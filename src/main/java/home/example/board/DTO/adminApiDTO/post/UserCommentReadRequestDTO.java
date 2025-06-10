package home.example.board.DTO.adminApiDTO.post;

import lombok.Data;

@Data
public class UserCommentReadRequestDTO {
    private long user_seq;
    private int offset;
    private int limit;
    private String sortType;
}
