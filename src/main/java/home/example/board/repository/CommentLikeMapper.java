package home.example.board.repository;

import home.example.board.domain.CommentLike;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentLikeMapper {
    void insertCommentLike(CommentLike commentLike);
    void deleteCommentLike(CommentLike commentLike);

    // selectCommentLikes 메서드 추가
    // Comment_seq_list, user_seq를 받아서 해당하는 CommentLike 리스트를 반환
    List<CommentLike> selectCommentLikes(Map<String, Object> params);
}
