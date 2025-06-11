package home.example.board.DTO.adminApiDTO.userComment;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserCommentReadResponseDTO {
    private List<?> commentList;
    private int totalCount;
    private int totalPage;
    private int currentPage;
    private int pageSize;
    private String sortType;
    private String error_message;
}
