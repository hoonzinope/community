package home.example.board.repository;

import home.example.board.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void addUser(User user);
    User getUser(String user_name);
}
