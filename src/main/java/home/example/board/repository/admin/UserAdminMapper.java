package home.example.board.repository.admin;

import home.example.board.DTO.adminApiDTO.UserReadAdminRequestDTO;
import home.example.board.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserAdminMapper {
    List<User> getUsers(UserReadAdminRequestDTO userReadAdminRequestDTO);
    int getUserCount(UserReadAdminRequestDTO userReadAdminRequestDTO);
}
