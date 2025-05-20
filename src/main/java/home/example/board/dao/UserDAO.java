package home.example.board.dao;

import home.example.board.DTO.BotUserDTO;
import home.example.board.domain.User;
import home.example.board.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Repository
public class UserDAO {

    private final UserMapper userMapper;

    @Autowired
    public UserDAO(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public BotUserDTO getBotUserByName(String user_name) {
        return userMapper.getBotUserByName(user_name);
    }

    public boolean isUserNameDuplicate(String user_name) {
        return userMapper.getUser(user_name) != null;
    }

    public User getUserByNickname(String user_nickname) {
        return userMapper.getUserByNickname(user_nickname);
    }

    public User getUserBySeq(Long user_seq) {
        return userMapper.getUserBySeq(user_seq);
    }

    public void addUser(String user_name, String user_pw, String user_email) {
        User newUser = User.builder()
                .user_name(user_name)
                .user_pw(user_pw)
                .user_email(user_email)
                .user_nickname(UUID.randomUUID().toString().substring(0, 8))
                .insert_ts(LocalDateTime.now())
                .build();
        userMapper.addUser(newUser);
    }

    public void updateUserNickNameAndEmail(Long user_seq, String user_nickname, String user_email) {
        User user = userMapper.getUserBySeq(user_seq);
        if (user != null) {
            user.setUser_nickname(user_nickname);
            user.setUser_email(user_email);
            userMapper.updateUserInfo(user);
        }
    }

    public void resetUserPassword(Long user_seq, String reset_pw) {
        User user = userMapper.getUserBySeq(user_seq);
        if (user != null) {
            user.setUser_pw(reset_pw);
            user.setForce_password_change(1);
            userMapper.updateUserPassword(user);
        }
    }

    public void updateUserPassword(Long user_seq, String user_pw) {
        User user = userMapper.getUserBySeq(user_seq);
        if (user != null) {
            user.setUser_pw(user_pw);
            user.setForce_password_change(0);
            userMapper.updateUserInfo(user);
        }
    }

    public void deleteUser(Long user_seq) {
        User user = userMapper.getUserBySeq(user_seq);
        if (user != null) {
            user.setDelete_flag(1);
            user.setDelete_ts(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            userMapper.deleteUser(user);
        }
    }

    public void restoreUser(Long user_seq) {
        User user = userMapper.getUserBySeq(user_seq);
        if (user != null) {
            user.setDelete_flag(0);
            user.setDelete_ts(null);
            userMapper.restoreUser(user);
        }
    }

    public void updateUserRole(Long user_seq, String role) {
        User user = userMapper.getUserBySeq(user_seq);
        if (user != null) {
            user.setRole(role);
            userMapper.updateUserInfo(user);
        }
    }
}
