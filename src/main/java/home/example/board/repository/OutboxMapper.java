package home.example.board.repository;

import home.example.board.domain.Outbox;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OutboxMapper {
    void insertOutbox(Outbox outbox);
}
