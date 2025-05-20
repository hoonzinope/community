package home.example.board.DTO.adminApiDTO;

import home.example.board.domain.Post;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserPostReadAdminResponseDTO {
    private List<Post> postList;
    private int totalCount;
    private int totalPage;
    private int currentPage;
    private int pageSize;
    private String sortType;
    private String error_message;
}
