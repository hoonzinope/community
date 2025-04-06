package home.example.board.repository;

import home.example.board.domain.Comment;
import home.example.board.domain.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentHistoryMapper {
    public void insertCommentHistory(long comment_seq);
}
