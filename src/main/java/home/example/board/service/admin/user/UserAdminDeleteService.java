package home.example.board.service.admin.user;

import home.example.board.dao.CommentDAO;
import home.example.board.dao.OutboxDAO;
import home.example.board.dao.PostDAO;
import home.example.board.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAdminDeleteService {

    private final UserDAO userDAO;
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final OutboxDAO outboxDAO;

    @Autowired
    public UserAdminDeleteService(UserDAO userDAO, PostDAO postDAO, CommentDAO commentDAO, OutboxDAO outboxDAO) {
        this.userDAO = userDAO;
        this.postDAO = postDAO;
        this.commentDAO = commentDAO;
        this.outboxDAO = outboxDAO;
    }

    public void deleteUser(long user_seq) {
        commentDAO.removeCommentAllByUser(user_seq);
        outboxDAO.removeCommentAllByUser(user_seq);

        postDAO.removePostAllByUser(user_seq);
        outboxDAO.removePostAllByUser(user_seq);

        userDAO.deleteUser(user_seq);
    }

}
