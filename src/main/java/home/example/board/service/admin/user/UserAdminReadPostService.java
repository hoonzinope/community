package home.example.board.service.admin.user;

import home.example.board.DTO.adminApiDTO.UserPostReadAdminRequestDTO;
import home.example.board.DTO.adminApiDTO.UserPostReadAdminResponseDTO;
import home.example.board.dao.admin.user.UserPostAdminDAO;
import home.example.board.domain.Post;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAdminReadPostService {

    private final UserPostAdminDAO userPostAdminDAO;

    public UserAdminReadPostService(UserPostAdminDAO userPostAdminDAO) {
        this.userPostAdminDAO = userPostAdminDAO;
    }

    public UserPostReadAdminResponseDTO getUserPost(
            UserPostReadAdminRequestDTO userPostReadAdminRequestDTO) {
        try {
            List<Post> postList = userPostAdminDAO.getUserPostPaging(userPostReadAdminRequestDTO);
            int totalCount = userPostAdminDAO.getUserPostCount(userPostReadAdminRequestDTO);
            return UserPostReadAdminResponseDTO.builder()
                    .postList(postList)
                    .totalCount(totalCount)
                    .totalPage((int) Math.ceil((double) totalCount / userPostReadAdminRequestDTO.getLimit()))
                    .currentPage(userPostReadAdminRequestDTO.getOffset() / userPostReadAdminRequestDTO.getLimit() + 1)
                    .pageSize(userPostReadAdminRequestDTO.getLimit())
                    .sortType(userPostReadAdminRequestDTO.getSortType())
                    .error_message(null)
                    .build();
        } catch (Exception e) {
            return UserPostReadAdminResponseDTO.builder()
                    .error_message("exception " + e.getMessage())
                    .build();
        }
    }

}
