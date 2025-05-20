package home.example.board.controller.api.admin.user;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.DTO.adminApiDTO.UserUpdateAdminRequestDTO;
import home.example.board.utils.CheckAdminUserUtils;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAdminUpdateAPI {


    @PutMapping("/admin/users/update")
    public ResponseEntity<JSONObject> updateUserInfoByAdmin(
            @RequestBody UserUpdateAdminRequestDTO userUpdateAdminRequestDTO,
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {
        // Implement the logic to update user info
        JSONObject jsonObject = new JSONObject();
        try {
            CheckAdminUserUtils.isAdminOrThrowException(customUserDetail);

            jsonObject.put("status", "success");
            jsonObject.put("message", "User updated successfully.");
            return ResponseEntity.ok().body(jsonObject);
        } catch (Exception e) {
            jsonObject.put("status", "error");
            jsonObject.put("message", "Failed to update user: " + e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
    }
}
