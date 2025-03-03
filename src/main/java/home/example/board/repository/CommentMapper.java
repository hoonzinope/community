package home.example.board.repository;

import home.example.board.DTO.CommentDTO;
import home.example.board.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    void insertComment(Comment comment);
    List<CommentDTO> selectComments(int post_seq);
    Comment selectComment(int comment_seq);
    void updateComment(Comment comment);
    void deleteComment(int comment_seq);
}
