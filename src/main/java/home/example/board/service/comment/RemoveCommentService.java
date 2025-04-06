package home.example.board.service.comment;

import home.example.board.dao.CommentDAO;
import home.example.board.dao.CommentHistoryDAO;
import home.example.board.dao.OutboxDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveCommentService {
    private final CommentDAO commentDAO;
    private final CommentHistoryDAO commentHistoryDAO;
    private final OutboxDAO outboxDAO;

    @Autowired
    public RemoveCommentService(CommentDAO commentDAO, CommentHistoryDAO commentHistoryDAO, OutboxDAO outboxDAO) {
        this.commentDAO = commentDAO;
        this.commentHistoryDAO = commentHistoryDAO;
        this.outboxDAO = outboxDAO;
    }

    @Transactional
    public void deleteComment(long comment_seq, long user_seq) throws IllegalAccessException {
        commentHistoryDAO.insertCommentHistory(comment_seq);
        commentDAO.deleteComment(comment_seq, user_seq);
        outboxDAO.insertComment(comment_seq, "", "DELETE");
    }
}
