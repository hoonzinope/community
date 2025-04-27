package home.example.board.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BotUserDTO {
    private Long user_seq;
    private String user_name;
    private String user_pw;
    private String user_email;
    private String user_nickname;
    private LocalDateTime insert_ts;
    private String role;
    private int delete_flag;
    private LocalDateTime delete_ts;
    private int force_password_change;
    private String is_bot;
}
