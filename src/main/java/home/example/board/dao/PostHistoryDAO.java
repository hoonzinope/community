package home.example.board.dao;

import home.example.board.repository.PostHistoryMapper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class PostHistoryDAO {

    @Autowired
    PostHistoryMapper postHistoryMapper;

    public void insertPostHistory(long post_seq) {
        postHistoryMapper.insertPostHistory(post_seq);
    }

}
