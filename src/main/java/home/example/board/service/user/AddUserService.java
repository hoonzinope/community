package home.example.board.service.user;

import home.example.board.dao.UserDAO;
import home.example.board.utils.UserInfoCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AddUserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AddUserService(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public void addUser(String user_name, String user_pw, String user_email) {
        if (isValidInput(user_name, user_pw, user_email) && isNotDuplicateUserName(user_name)) {
            userDAO.addUser(user_name, passwordEncoder.encode(user_pw), user_email);
        } else {
            throw new IllegalArgumentException("user_name, user_pw, user_email is not valid");
        }
    }

    private boolean isValidInput(String user_name, String user_pw, String user_email) {
        return UserInfoCheckUtils.isValidate(user_name, user_pw, user_email);
    }

    private boolean isNotDuplicateUserName(String user_name) {
        return !userDAO.isUserNameDuplicate(user_name);
    }
}
