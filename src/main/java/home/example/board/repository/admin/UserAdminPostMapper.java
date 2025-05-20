package home.example.board.repository.admin;

import home.example.board.DTO.adminApiDTO.UserPostReadAdminRequestDTO;
import home.example.board.DTO.adminApiDTO.UserReadAdminRequestDTO;
import home.example.board.domain.Post;
import home.example.board.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserAdminPostMapper {
    List<Post> getPostByUserSeq(UserPostReadAdminRequestDTO userPostReadAdminRequestDTO);
    int getPostCountByUserSeq(UserPostReadAdminRequestDTO UserPostReadAdminRequestDTO);
}
