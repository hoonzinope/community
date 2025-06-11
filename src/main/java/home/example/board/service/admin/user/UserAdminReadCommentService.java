package home.example.board.service.admin.user;

import home.example.board.DTO.adminApiDTO.userComment.AdminUserCommentPagingDTO;
import home.example.board.DTO.adminApiDTO.userComment.UserCommentReadRequestDTO;
import home.example.board.DTO.adminApiDTO.userComment.UserCommentReadResponseDTO;
import home.example.board.dao.admin.user.UserCommentAdminDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAdminReadCommentService {
    private final UserCommentAdminDAO userCommentAdminDAO;
    public UserAdminReadCommentService(UserCommentAdminDAO userCommentAdminDAO) {
        this.userCommentAdminDAO = userCommentAdminDAO;
    }

    public UserCommentReadResponseDTO getUserComment(UserCommentReadRequestDTO userCommentReadRequestDTO) {
        try {
            List<AdminUserCommentPagingDTO> commentList = userCommentAdminDAO.getUserCommentPaging(userCommentReadRequestDTO);
            int totalCount = userCommentAdminDAO.getUserCommentCount(userCommentReadRequestDTO);
            return UserCommentReadResponseDTO.builder()
                    .commentList(commentList)
                    .totalCount(totalCount)
                    .totalPage((int) Math.ceil((double) totalCount / userCommentReadRequestDTO.getLimit()))
                    .currentPage(userCommentReadRequestDTO.getOffset() / userCommentReadRequestDTO.getLimit() + 1)
                    .pageSize(userCommentReadRequestDTO.getLimit())
                    .sortType(userCommentReadRequestDTO.getSortType())
                    .error_message(null)
                    .build();
        } catch (Exception e) {
            return UserCommentReadResponseDTO.builder()
                    .error_message("exception " + e.getMessage())
                    .build();
        }
    }
}
