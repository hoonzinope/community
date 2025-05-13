package home.example.board.DTO.adminApiDTO;

import lombok.Data;

@Data
public class UserReadAdminRequestDTO {
    private String userRole;
    private String userStatus;
    private String sortType;
    private int limit;
    private int offset;
    private String searchType;
    private String searchValue;
}
