package home.example.board.dao.admin.user;

import home.example.board.DTO.adminApiDTO.UserReadAdminRequestDTO;
import home.example.board.domain.User;
import home.example.board.repository.admin.UserAdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAdminDAO {

    private final UserAdminMapper userAdminMapper;

    @Autowired
    public UserAdminDAO(UserAdminMapper userAdminMapper) {
        this.userAdminMapper = userAdminMapper;
    }

    public List<User> getUsers(UserReadAdminRequestDTO userReadAdminRequestDTO) {
        // Implement the logic to get users from the database
        return userAdminMapper.getUsers(userReadAdminRequestDTO);
    }

    public int getUserCount(UserReadAdminRequestDTO userReadAdminRequestDTO) {
        // Implement the logic to get total count of users from the database
        return userAdminMapper.getUserCount(userReadAdminRequestDTO);
    }

    public User getUser(long user_seq) {
        // Implement the logic to get a user by ID from the database
        return userAdminMapper.getUser(user_seq);
    }

    public void updateUserInfo(User user) {
        // Implement the logic to update user info in the database
        int count = userAdminMapper.checkDuplicateUserInfo(user);
        if(count != 0) {
            throw new IllegalArgumentException("User with the same email or nickname or id already exists.");
        }
        userAdminMapper.updateUserInfo(user);
    }
}
