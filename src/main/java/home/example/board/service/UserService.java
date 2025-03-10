package home.example.board.service;

import home.example.board.domain.User;
import home.example.board.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Model insertUserInfo(Model model, long user_seq) {
        User user = userMapper.getUserBySeq(user_seq);
        model.addAttribute("user_seq", user.getUser_seq());
        model.addAttribute("user_name", user.getUser_name());
        model.addAttribute("user_email", user.getUser_email());
        model.addAttribute("user_nickname", user.getUser_nickname().substring(0,8));
        model.addAttribute("insert_ts", user.getInsert_ts());
        model.addAttribute("role", user.getRole());
        model.addAttribute("delete_flag", user.getDelete_flag());
        model.addAttribute("delete_ts", user.getDelete_ts());
        return model;
    }

    public void addUsers(String user_name, String user_pw, String user_email) {
        checkExceptionInput(user_name, user_pw, user_email);
        if(isValidate(user_name, user_pw, user_email)) {
            User newUser = User.builder()
                    .user_name(user_name)
                    .user_pw(passwordEncoder.encode(user_pw))
                    .user_email(user_email)
                    .user_nickname(UUID.randomUUID().toString())
                    .insert_ts(LocalDateTime.now())
                    .build();
            userMapper.addUser(newUser);
        }else{
            throw new IllegalArgumentException("user_name, user_pw, user_email is not valid");
        }
    }

    private void checkExceptionInput(String user_name, String user_pw, String user_email) throws IllegalArgumentException {
        if(user_name == null || user_pw == null || user_email == null) {
            throw new IllegalArgumentException("user_name, user_pw, user_email is required");
        }

        if(user_name.length() < 4 || user_name.length() > 20) {
            throw new IllegalArgumentException("user_name length must be between 4 and 20");
        }

        if(user_pw.length() < 8 || user_pw.length() > 20) {
            throw new IllegalArgumentException("user_pw length must be between 8 and 20");
        }

        if(user_email.length() < 10 || user_email.length() > 50) {
            throw new IllegalArgumentException("user_email length must be between 10 and 50");
        }

        if(userMapper.getUser(user_name) != null) {
            throw new IllegalArgumentException("user_name already exists");
        }
    }

    private boolean isValidate(String user_name, String user_pw, String user_email) {
        if(!isUsername(user_name)){
            System.out.println("user_name is not valid");
        }
        if (!isPassword(user_pw)){
            System.out.println("user_pw is not valid");
        }
        if (!isEmail(user_email)){
            System.out.println("user_email is not valid");
        }
        return isUsername(user_name) && isPassword(user_pw) && isEmail(user_email);
    }
    private boolean isUsername(String username) {
        return username.matches("^[a-zA-Z0-9]{4,20}$");
    }
    private boolean isPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$");
    }
    private boolean isEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    public void updateUserInfo(long user_seq, String userNickname, String userEmail) {
        if(userNickname == null && userEmail == null) {
            throw new IllegalArgumentException("user_nickname, user_email is required");
        }

        if(userNickname != null && (userNickname.length() < 4 || userNickname.length() > 20)) {
            throw new IllegalArgumentException("user_nickname length must be between 4 and 20");
        }

        if(userEmail != null && (userEmail.length() < 10 || userEmail.length() > 50)) {
            throw new IllegalArgumentException("user_email length must be between 10 and 50");
        }

        User user = userMapper.getUserBySeq(user_seq);
        user.setUser_nickname(userNickname);
        user.setUser_email(userEmail);

        userMapper.updateUserInfo(user);
    }

    public void userPasswordReset(long userSeq) {
        User user = userMapper.getUserBySeq(userSeq);
        if(user == null) {
            throw new IllegalArgumentException("user not found");
        }
        user.setUser_pw(passwordEncoder.encode("0000"));
        userMapper.resetUserPw(user);
    }

    public void userPasswordUpdate(long userSeq, String newPw) {
        User user = userMapper.getUserBySeq(userSeq);
        if(user == null) {
            throw new IllegalArgumentException("user not found");
        }
        if(!isPassword(newPw)) {
            throw new IllegalArgumentException("new_pw is not valid");
        }
        user.setUser_pw(passwordEncoder.encode(newPw));
        userMapper.updateUserInfo(user);
    }

    public void userDelete(long userSeq) {
        User user = userMapper.getUserBySeq(userSeq);
        if(user == null) {
            throw new IllegalArgumentException("user not found");
        }
        user.setDelete_flag(1);
        user.setDelete_ts(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        userMapper.updateUserInfo(user);
    }
}
