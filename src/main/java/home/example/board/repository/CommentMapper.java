package home.example.board.repository;

import home.example.board.DTO.CommentDTO;
import home.example.board.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    Long insertComment(Comment comment);
    List<CommentDTO> selectComments(long post_seq);
    Comment selectComment(long comment_seq);
    void updateComment(Comment comment);
    void deleteComment(long comment_seq);
    List<Comment> getCommentAll();
}
