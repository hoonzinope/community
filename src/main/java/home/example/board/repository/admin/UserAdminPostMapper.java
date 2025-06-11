package home.example.board.repository.admin;

import home.example.board.DTO.adminApiDTO.userPost.UserPostReadAdminRequestDTO;
import home.example.board.DTO.adminApiDTO.userPost.AdminUserPostPagingDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserAdminPostMapper {
    List<AdminUserPostPagingDTO> getPostByUserSeq(UserPostReadAdminRequestDTO userPostReadAdminRequestDTO);
    int getPostCountByUserSeq(UserPostReadAdminRequestDTO UserPostReadAdminRequestDTO);
}
