package home.example.board.repository;

import home.example.board.DTO.BotUserDTO;
import home.example.board.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    BotUserDTO getBotUserByName(String user_name);
    void addUser(User user);
    User getUser(String user_name);
    List<User> getUserAll();
    void updateUserInfo(User user);
    void updateUserPassword(User user);
    User getUserBySeq(long userSeq);
    List<User> getUserBySeqList(List<Long> userSeqList);
    void resetUserPw(User user);
    void deleteUser(User user);
    void restoreUser(User user);
    User getUserByNickname(String user_nickname);
}
