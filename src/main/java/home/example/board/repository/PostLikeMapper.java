package home.example.board.repository;

import home.example.board.domain.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface PostLikeMapper {
    void insertPostLike(PostLike postLike);
    void deletePostLike(PostLike postLike);
    int countPostLike(
            @Param("post_seq") long post_seq,
            @Param("like_type") String like_type); // like_type, total_cnt
    PostLike selectPostLike(PostLike postLike);
}
