package home.example.board.repository;

import home.example.board.domain.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostHistoryMapper {

    public void insertPostHistory(Post post);

}
