package home.example.board.DTO.adminApiDTO;

import lombok.Data;

@Data
public class UserReadAdminRequestDTO {
    private String userRole;
    private Integer delete_flag;
    private String sortType;
    private int limit;
    private int offset;
    private String searchType;
    private String searchValue;
}
