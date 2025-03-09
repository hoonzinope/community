package home.example.board.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class User {

    private long user_seq;
    private String user_name;
    private String user_pw;
    private String user_email;
    private String user_nickname;
    private LocalDateTime insert_ts;
    private String role;
    private int delete_flag;
    private LocalDateTime delete_ts;
}
