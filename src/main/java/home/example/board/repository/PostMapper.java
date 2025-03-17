package home.example.board.repository;

import home.example.board.DTO.PostPagingDTO;
import home.example.board.domain.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {
    int getPostTotalSize();
    int getPostTotalSizeByCategory(Map<String, Object> requestMap);
    List<PostPagingDTO> getPostListPaging(Map<String, Object> paging);
    List<PostPagingDTO> getPostListByCategory(Map<String, Object> requestMap);
    Post getPost(long post_seq);
    void insertPost(Post post);
    void updatePost(Post post);
    void deletePost(long post_seq);

    void updateViewCount(long post_seq);
}
