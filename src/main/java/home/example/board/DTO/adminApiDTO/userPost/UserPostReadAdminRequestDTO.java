package home.example.board.DTO.adminApiDTO.userPost;

import lombok.Data;

@Data
public class UserPostReadAdminRequestDTO {
    private long user_seq;
    private int offset;
    private int limit;
    private String sortType;
}
