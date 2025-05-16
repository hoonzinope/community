package home.example.board.controller.api.admin.user;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.service.user.UpdateUserService;
import home.example.board.utils.CheckAdminUserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserAdminPwResetAPI {

    private final UpdateUserService updateUserService;

    @Autowired
    public UserAdminPwResetAPI(UpdateUserService updateUserService) {
        this.updateUserService = updateUserService;
    }


    @Operation(summary = "Reset user password for admin", description = "Reset user password for admin")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
    })
    @GetMapping("/admin/users/resetpw/{userSeq}")
    public ResponseEntity<JSONObject> resetPassword(
            @PathVariable Long userSeq,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            CheckAdminUserUtils.isAdminOrThrowException(userDetail);
            updateUserService.userPasswordReset(userSeq);
            jsonObject.put("status", "success");
            jsonObject.put("message", "Password reset successfully.");
            return ResponseEntity.ok().body(jsonObject);
        }catch (Exception e) {
            log.error(e.getMessage());
            jsonObject.put("status", "error");
            jsonObject.put("message", "Password reset error " + e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
    }
}
