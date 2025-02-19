package home.example.board.repository;

import home.example.board.domain.CommentLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentLikeMapper {
    void insertCommentLike(CommentLike commentLike);
    void deleteCommentLike(CommentLike commentLike);
}
