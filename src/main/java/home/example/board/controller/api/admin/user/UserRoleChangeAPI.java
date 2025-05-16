package home.example.board.controller.api.admin.user;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.service.admin.user.UserAdminRoleChangeService;
import home.example.board.utils.CheckAdminUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserRoleChangeAPI {

    private final UserAdminRoleChangeService userAdminRoleChangeService;

    @Autowired
    public UserRoleChangeAPI(UserAdminRoleChangeService userAdminRoleChangeService) {
        this.userAdminRoleChangeService = userAdminRoleChangeService;
    }

    @PatchMapping("/admin/users/roleChange/{userSeq}")
    public ResponseEntity<JSONObject> changeUserRole(
            @PathVariable Long userSeq,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        CheckAdminUserUtils.isAdminOrThrowException(userDetail);
        // TODO : Implement change user role logic
        JSONObject jsonObject = new JSONObject();
        try{
            // Call the service to change the user role
            userAdminRoleChangeService.changeUserRole(userSeq);
            jsonObject.put("status", "success");
            jsonObject.put("message", "User role changed successfully");
            return ResponseEntity.ok(jsonObject);
        } catch (Exception e) {
            log.error("Error changing user role: ", e);
            jsonObject.put("status", "error");
            jsonObject.put("message", "Failed to change user role");
            return ResponseEntity.status(500).body(jsonObject);
        }
    }
}
