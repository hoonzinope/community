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

        model.addAttribute("user_seq", user.getUser_seq());
        model.addAttribute("user_name", user.getUser_name());
        model.addAttribute("user_email", user.getUser_email());
        String nickName = user.getUser_nickname();
        nickName = nickName.contains("-") ? nickName.substring(0,8) : nickName;
        model.addAttribute("user_nickname", nickName);
        model.addAttribute("insert_ts", user.getInsert_ts());
        model.addAttribute("role", user.getRole());
        model.addAttribute("delete_flag", user.getDelete_flag());
        model.addAttribute("delete_ts", user.getDelete_ts());
        return model;
    }
}
