package home.example.board.runner;

import home.example.board.domain.User;
import home.example.board.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "local")
public class PasswordEncryptRunner implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {
        List<User> users = userMapper.getUserAll();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        for(User user : users) {
            String pw = user.getUser_pw();
            if(!pw.startsWith("$2a$")){
                String encode_pw = encoder.encode(pw);
                user.setUser_pw(encode_pw);
            }
            userMapper.updateUserInfo(user);
        }
    }
}
