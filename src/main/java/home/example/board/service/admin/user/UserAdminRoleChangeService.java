package home.example.board.service.admin.user;

import home.example.board.dao.UserDAO;
import home.example.board.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAdminRoleChangeService {

    private final UserDAO userDAO;

    @Autowired
    public UserAdminRoleChangeService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void changeUserRole(Long userSeq) {
        User user = userDAO.getUserBySeq(userSeq);
        if(user == null)
            throw new IllegalArgumentException("User not found with seq: " + userSeq);
        String oldRole = user.getRole() == "ADMIN" ? "USER" : "ADMIN";
        userDAO.updateUserRole(userSeq, oldRole);
    }
}
