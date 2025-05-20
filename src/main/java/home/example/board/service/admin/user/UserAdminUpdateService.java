package home.example.board.service.admin.user;

import home.example.board.DTO.adminApiDTO.UserUpdateAdminRequestDTO;
import home.example.board.dao.UserDAO;
import home.example.board.dao.admin.user.UserAdminDAO;
import home.example.board.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAdminUpdateService {

    private final UserAdminDAO userAdminDAO;

    @Autowired
    public UserAdminUpdateService(UserAdminDAO userAdminDAO) {
        this.userAdminDAO = userAdminDAO;
    }

    public void updateUserInfoByAdmin(UserUpdateAdminRequestDTO userUpdateAdminRequestDTO) {
        // Implement the logic to update user info
        long user_seq = userUpdateAdminRequestDTO.getUser_seq();
        String user_email = userUpdateAdminRequestDTO.getUser_email();
        String user_nickname = userUpdateAdminRequestDTO.getUser_nickname();
        String user_name = userUpdateAdminRequestDTO.getUser_name();
        String user_role = userUpdateAdminRequestDTO.getUser_role();

        User user = userAdminDAO.getUser(user_seq);
        if (user == null) {
            throw new IllegalArgumentException("User not found with seq: " + user_seq);
        }
        user.setUser_email(user_email);
        user.setUser_nickname(user_nickname);
        user.setUser_name(user_name);
        user.setRole(user_role);
        // userAdminDAO.updateUserInfo(userUpdateAdminRequestDTO);
        userAdminDAO.updateUserInfo(user);
    }
}
