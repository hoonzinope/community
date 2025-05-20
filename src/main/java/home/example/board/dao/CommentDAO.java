package home.example.board.dao;

import home.example.board.DTO.CommentDTO;
import home.example.board.domain.Comment;
import home.example.board.repository.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CommentDAO {

    private final CommentMapper commentMapper;

    @Autowired
    public CommentDAO(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public List<CommentDTO> selectComments(long post_seq) {
        return commentMapper.selectComments(post_seq);
    }

    public List<Comment> selectUserComments(long user_seq, int offset, int limit) {
        return commentMapper.selectUserCommentsPaging(user_seq, offset, limit);
    }

    public int selectUserCommentsCount(long user_seq) {
        return commentMapper.selectUserCommentsCount(user_seq);
    }

    @Transactional
    public Long insertComment(long post_seq, String content, Long parent_comment_seq, long user_seq) {
        Comment comment = Comment.builder()
                .post_seq(post_seq)
                .content(content)
                .parent_comment_seq(parent_comment_seq)
                .user_seq(user_seq)
                .build();
        commentMapper.insertComment(comment);
        return comment.getComment_seq();
    }

    @Transactional
    public Long insertComment(long post_seq, String content, Long parent_comment_seq, Long reply_user_seq, long user_seq) {
        Comment comment = Comment.builder()
                .post_seq(post_seq)
                .content(content)
                .parent_comment_seq(parent_comment_seq)
                .reply_user_seq(reply_user_seq)
                .user_seq(user_seq)
                .build();
        commentMapper.insertComment(comment);
        return comment.getComment_seq();
    }

    @Transactional
    public void updateComment(long comment_seq, String content, long user_seq) {
        Comment comment = commentMapper.selectComment(comment_seq);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found with seq: " + comment_seq);
        }
        if (user_seq != comment.getUser_seq()) {
            throw new IllegalArgumentException("User does not have permission to update this comment");
        }
        comment.setContent(content);
        commentMapper.updateComment(comment);
    }

    @Transactional
    public void deleteComment(long comment_seq, long user_seq) {
        Comment comment = commentMapper.selectComment(comment_seq);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found with seq: " + comment_seq);
        }
        if (user_seq != comment.getUser_seq()) {
            throw new IllegalArgumentException("User does not have permission to delete this comment");
        }
        commentMapper.deleteComment(comment_seq);
    }

    @Transactional
    public void removeCommentAllByUser(long user_seq) {
        commentMapper.removeCommentAllByUser(user_seq);
    }

    @Transactional
    public void restoreCommentAllByUser(long user_seq) {
        commentMapper.restoreCommentAllByUser(user_seq);
    }
}
