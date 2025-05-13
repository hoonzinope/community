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

}
