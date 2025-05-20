package home.example.board.DTO.adminApiDTO;

import lombok.Builder;
import lombok.Data;
import org.json.simple.JSONObject;

import java.util.List;

@Data
@Builder
public class UserReadAdminResponseDTO {
    private List<JSONObject> userList;
    private int totalCount;
    private int totalPage;
    private int currentPage;
    private int pageSize;
    private String userRole;
    private String userStatus;
    private String sortType;
    private String sortOrder;
    private String searchType;
    private String searchValue;
    private String error_message;
}
