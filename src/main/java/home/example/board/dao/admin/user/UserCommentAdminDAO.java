package home.example.board.dao.admin.user;

import home.example.board.DTO.adminApiDTO.userComment.AdminUserCommentPagingDTO;
import home.example.board.DTO.adminApiDTO.userComment.UserCommentReadRequestDTO;
import home.example.board.repository.admin.UserAdminCommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCommentAdminDAO {
    private final UserAdminCommentMapper userAdminCommentMapper;
    public UserCommentAdminDAO(UserAdminCommentMapper userAdminCommentMapper) {
        this.userAdminCommentMapper = userAdminCommentMapper;
    }

    public List<AdminUserCommentPagingDTO> getUserCommentPaging(
            UserCommentReadRequestDTO userCommentReadRequestDTO) {
        return userAdminCommentMapper.getCommentByUserSeq(userCommentReadRequestDTO);
    }

    public int getUserCommentCount(
            UserCommentReadRequestDTO userCommentReadRequestDTO) {
        return userAdminCommentMapper.getCommentCountByUserSeq(userCommentReadRequestDTO);
    }
}
