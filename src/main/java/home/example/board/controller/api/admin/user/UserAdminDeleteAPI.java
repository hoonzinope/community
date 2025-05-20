package home.example.board.controller.api.admin.user;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.service.admin.user.UserAdminDeleteService;
import home.example.board.utils.CheckAdminUserUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAdminDeleteAPI {

    private final UserAdminDeleteService userAdminDeleteService;

    @Autowired
    public UserAdminDeleteAPI(UserAdminDeleteService userAdminDeleteService) {
        this.userAdminDeleteService = userAdminDeleteService;
    }

    @DeleteMapping("/admin/users/delete/{userSeq}")
    public ResponseEntity<JSONObject> deleteUser(
            @PathVariable Long userSeq,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            CheckAdminUserUtils.isAdminOrThrowException(userDetail);
            userAdminDeleteService.deleteUser(userSeq);
            jsonObject.put("status", "success");
            jsonObject.put("message", "User deleted successfully.");
            return ResponseEntity.ok().body(jsonObject);
        } catch (Exception e) {
            jsonObject.put("status", "error");
            jsonObject.put("message", "Failed to delete user: " + e.getMessage());
            return ResponseEntity.status(500).body(jsonObject);
        }
    }
}
