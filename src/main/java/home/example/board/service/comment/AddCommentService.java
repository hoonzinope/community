package home.example.board.service.comment;

import home.example.board.dao.CommentDAO;
import home.example.board.dao.CommentHistoryDAO;
import home.example.board.dao.OutboxDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddCommentService {

    private final CommentDAO commentDAO;
    private final CommentHistoryDAO commentHistoryDAO;
    private final OutboxDAO outboxDAO;

    @Autowired
    public AddCommentService(
            CommentDAO commentDAO,
            CommentHistoryDAO commentHistoryDAO,
            OutboxDAO outboxDAO) {
        this.commentDAO = commentDAO;
        this.commentHistoryDAO = commentHistoryDAO;
        this.outboxDAO = outboxDAO;
    }

    @Transactional
    public void addComment(
            long post_seq,
            String content,
            Long parent_comment_seq, long user_seq) {
        long comment_seq = commentDAO.insertComment(post_seq, content, parent_comment_seq, user_seq);
        outboxDAO.insertComment(comment_seq, content,"INSERT");
    }
}
