package home.example.board.controller.api.admin.user;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.service.admin.user.UserAdminRestoreService;
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
public class UserAdminRestoreAPI {

    private final UserAdminRestoreService userAdminRestoreService;

    @Autowired
    public UserAdminRestoreAPI(UserAdminRestoreService userAdminRestoreService) {
        this.userAdminRestoreService = userAdminRestoreService;
    }

    @PatchMapping("/admin/users/restore/{userSeq}")
    public ResponseEntity<JSONObject> restoreUser(
            @PathVariable Long userSeq,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        JSONObject jsonObject = new JSONObject();
        try{
            CheckAdminUserUtils.isAdminOrThrowException(userDetail);
            userAdminRestoreService.restoreUser(userSeq);
            jsonObject.put("status", "success");
            jsonObject.put("message", "User restored successfully.");
            return ResponseEntity.ok().body(jsonObject);
        } catch (Exception e) {
            log.error("Error restoring user: {}", e.getMessage());
            jsonObject.put("status", "error");
            jsonObject.put("message", "Failed to restore user: " + e.getMessage());
            return ResponseEntity.status(500).body(jsonObject);
        }
    }
}
