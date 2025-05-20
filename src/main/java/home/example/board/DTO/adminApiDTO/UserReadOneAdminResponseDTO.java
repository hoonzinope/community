package home.example.board.DTO.adminApiDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserReadOneAdminResponseDTO {
    long user_seq;
    String user_name;
    String user_email;
    String user_role;
    String user_status;
    String user_nickname;
    LocalDateTime user_insert_ts;
    LocalDateTime user_update_ts;
    String user_is_bot;
    String user_forced_password_change;
    String error_message;
}
