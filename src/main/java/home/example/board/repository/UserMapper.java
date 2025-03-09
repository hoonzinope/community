package home.example.board.repository;

import home.example.board.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    void addUser(User user);
    User getUser(String user_name);
    List<User> getUserAll();
    void updateUserInfo(User user);

    User getUserBySeq(long userSeq);
    List<User> getUserBySeqList(List<Long> userSeqList);
}
