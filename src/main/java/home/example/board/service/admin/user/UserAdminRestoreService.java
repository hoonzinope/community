package home.example.board.service.admin.user;

import home.example.board.dao.CommentDAO;
import home.example.board.dao.OutboxDAO;
import home.example.board.dao.PostDAO;
import home.example.board.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAdminRestoreService {

    private final UserDAO userDAO;
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final OutboxDAO outboxDAO;

    @Autowired
    public UserAdminRestoreService(UserDAO userDAO, PostDAO postDAO, CommentDAO commentDAO, OutboxDAO outboxDAO) {
        this.userDAO = userDAO;
        this.postDAO = postDAO;
        this.commentDAO = commentDAO;
        this.outboxDAO = outboxDAO;
    }

    public void restoreUser(long user_seq) {
        commentDAO.restoreCommentAllByUser(user_seq);
        outboxDAO.restoreCommentAllByUser(user_seq);

        postDAO.restorePostAllByUser(user_seq);
        outboxDAO.restorePostAllByUser(user_seq);

        userDAO.restoreUser(user_seq);
    }
}
