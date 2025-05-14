package home.example.board.service.admin.user;

import home.example.board.DTO.adminApiDTO.UserReadAdminRequestDTO;
import home.example.board.DTO.adminApiDTO.UserReadAdminResponseDTO;
import home.example.board.dao.admin.user.UserAdminDAO;
import home.example.board.domain.User;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserAdminReadService {

    private final UserAdminDAO userAdminDAO;

    @Autowired
    public UserAdminReadService(UserAdminDAO userAdminDAO) {
        this.userAdminDAO = userAdminDAO;
    }

    public UserReadAdminResponseDTO getUsers(UserReadAdminRequestDTO userReadAdminRequestDTO) {
        // Implement the logic to get users
        // dao logic

        List<User> users = userAdminDAO.getUsers(userReadAdminRequestDTO);
        int totalCount = userAdminDAO.getUserCount(userReadAdminRequestDTO);

        // Create and return the response DTO
        return UserReadAdminResponseDTO.builder()
                .userList(removeUserPrivateInfoAndConvertToJson(users))
                .totalCount(totalCount)
                .totalPage((int) Math.ceil((double) totalCount / userReadAdminRequestDTO.getLimit()))
                .currentPage(userReadAdminRequestDTO.getOffset() / userReadAdminRequestDTO.getLimit() + 1)
                .pageSize(userReadAdminRequestDTO.getLimit())
                .userRole(userReadAdminRequestDTO.getUserRole())
                .userStatus(userReadAdminRequestDTO.getDelete_flag() == null ? "null" : userReadAdminRequestDTO.getDelete_flag().toString())
                .sortType(userReadAdminRequestDTO.getSortType())
                .searchType(userReadAdminRequestDTO.getSearchType())
                .searchValue(userReadAdminRequestDTO.getSearchValue())
                .build();
    }

    public List<JSONObject> removeUserPrivateInfoAndConvertToJson(List<User> users) {
        // Implement the logic to remove private info and convert to JSON
        List<JSONObject> jsonList = new ArrayList<>();
        for (User user : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_seq", user.getUser_seq());
            jsonObject.put("user_name", user.getUser_name());
            jsonObject.put("user_email", user.getUser_email());
            jsonObject.put("user_role", user.getRole());
            jsonObject.put("user_status", user.getDelete_flag());
            jsonObject.put("user_nickname", user.getUser_nickname());
            jsonObject.put("user_insert_ts", user.getInsert_ts());
            jsonObject.put("user_update_ts", user.getUpdate_ts());
            jsonObject.put("user_is_bot", user.getIs_bot());
            jsonList.add(jsonObject);
        }
        return jsonList;
    }

}
