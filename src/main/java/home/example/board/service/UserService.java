package home.example.board.service;

import home.example.board.domain.User;
import home.example.board.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String user_name, String user_pw) {
        System.out.println("login");
        User user = userMapper.getUser(user_name);
        if(user == null) {
            throw new IllegalArgumentException("user_name does not exist");
        }
        if(!user.getUser_pw().equals(user_pw)) {
            throw new IllegalArgumentException("user_pw is not correct");
        }

        return user;
    }

    public void addUsers(String user_name, String user_pw, String user_email) {
        System.out.println("addUsers");
        checkExceptionInput(user_name, user_pw, user_email);
        if(isValidate(user_name, user_pw, user_email)) {
            User newUser = User.builder()
                    .user_name(user_name)
                    .user_pw(user_pw)
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

}
