package home.example.board.repository;

import home.example.board.domain.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface PostHistoryMapper {
    public void insertPostHistory(long post_seq);
}
