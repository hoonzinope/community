package home.example.board.repository;

import home.example.board.DTO.CommentDTO;
import home.example.board.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    void insertComment(Comment comment);
    List<CommentDTO> selectComments(long post_seq);
    Comment selectComment(long comment_seq);
    void updateComment(Comment comment);
    void deleteComment(long comment_seq);
    List<Comment> getCommentAll();

    List<Comment> selectUserCommentsPaging(long user_seq, int offset, int limit);
    int selectUserCommentsCount(long user_seq);
    void removeCommentAllByUser(long user_seq);
    void restoreCommentAllByUser(long user_seq);
}
