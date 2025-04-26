package home.example.board.service.user;

import home.example.board.dao.UserDAO;
import home.example.board.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class ReadUserService {

    private final UserDAO userDAO;

    @Autowired
    public ReadUserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Model getUserInfo(Model model, long user_seq) {
        User user = userDAO.getUserBySeq(user_seq);
        return this.userToModel(user, model);
    }

    public Model getUserInfo(Model model, String user_name) {
        User user = userDAO.getUserByNickname(user_name);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return this.userToModel(user, model);
    }

    private Model userToModel(User user, Model model) {
        model.addAttribute("user_seq", user.getUser_seq());
        model.addAttribute("user_name", user.getUser_name());
        model.addAttribute("user_email", user.getUser_email());
        model.addAttribute("user_nickname", user.getUser_nickname());
        model.addAttribute("insert_ts", user.getInsert_ts());
        model.addAttribute("role", user.getRole());
        model.addAttribute("delete_flag", user.getDelete_flag());
        model.addAttribute("delete_ts", user.getDelete_ts());
        return model;
    }
}
