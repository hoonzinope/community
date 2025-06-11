package home.example.board.dao.admin.user;

import home.example.board.DTO.adminApiDTO.userPost.UserPostReadAdminRequestDTO;
import home.example.board.DTO.adminApiDTO.userPost.AdminUserPostPagingDTO;
import home.example.board.repository.admin.UserAdminPostMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPostAdminDAO {

    private final UserAdminPostMapper userAdminPostMapper;

    public UserPostAdminDAO(UserAdminPostMapper userAdminPostMapper) {
        this.userAdminPostMapper = userAdminPostMapper;
    }

    public List<AdminUserPostPagingDTO> getUserPostPaging(
            UserPostReadAdminRequestDTO userPostReadAdminRequestDTO) {
        return userAdminPostMapper.getPostByUserSeq(userPostReadAdminRequestDTO);
    }

    public int getUserPostCount(
            UserPostReadAdminRequestDTO userPostReadAdminRequestDTO) {
        return userAdminPostMapper.getPostCountByUserSeq(userPostReadAdminRequestDTO);
    }
}
