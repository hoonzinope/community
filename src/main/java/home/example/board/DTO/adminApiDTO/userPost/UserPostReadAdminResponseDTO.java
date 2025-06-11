package home.example.board.DTO.adminApiDTO.userPost;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserPostReadAdminResponseDTO {
    private List<AdminUserPostPagingDTO> postList;
    private int totalCount;
    private int totalPage;
    private int currentPage;
    private int pageSize;
    private String sortType;
    private String error_message;
}
