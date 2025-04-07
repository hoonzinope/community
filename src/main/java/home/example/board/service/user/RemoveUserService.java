package home.example.board.service.user;

import home.example.board.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoveUserService {

    private final UserDAO userDAO;

    @Autowired
    public RemoveUserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void removeUser(long user_seq) {
        userDAO.deleteUser(user_seq);
    }
}
