package home.example.board.DTO.adminApiDTO;

import lombok.Data;

@Data
public class UserUpdateAdminRequestDTO {
    private Long user_seq;
    private String user_email;
    private String user_nickname;
    private String user_name;
    private String user_role;
}
