package home.example.board.service.user;

import home.example.board.dao.UserDAO;
import home.example.board.utils.UserInfoCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserService {

    private final UserDAO userDAO;
    private final PasswordEncoder encoder;

    @Autowired
    public UpdateUserService(UserDAO userDAO, PasswordEncoder encoder) {
        this.userDAO = userDAO;
        this.encoder = encoder;
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

        userDAO.updateUserNickNameAndEmail(user_seq, userNickname, userEmail);
    }

    public void userPasswordReset(long user_seq) {
        userDAO.resetUserPassword(user_seq, encoder.encode("0000"));
    }

    public void userPasswordUpdate(long userSeq, String newPw) {
        if(!UserInfoCheckUtils.isPassword(newPw)) {
            throw new IllegalArgumentException("new_pw is not valid");
        }
        userDAO.updateUserPassword(userSeq, encoder.encode(newPw));
    }
}
