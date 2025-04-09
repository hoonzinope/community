package home.example.board.dao;

import home.example.board.repository.CommentHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommentHistoryDAO {
    private final CommentHistoryMapper commentHistoryMapper;

    @Autowired
    public CommentHistoryDAO(CommentHistoryMapper commentHistoryMapper) {
        this.commentHistoryMapper = commentHistoryMapper;
    }

    public void insertCommentHistory(long comment_seq) {
        commentHistoryMapper.insertCommentHistory(comment_seq);
    }
}
