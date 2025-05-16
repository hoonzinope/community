package home.example.board.controller.api.admin.user;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.DTO.adminApiDTO.UserReadAdminRequestDTO;
import home.example.board.DTO.adminApiDTO.UserReadAdminResponseDTO;
import home.example.board.service.admin.user.UserAdminReadService;
import home.example.board.utils.CheckAdminUserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserAdminReadAPI {

    private final UserAdminReadService userAdminReadService;

    @Autowired
    public UserAdminReadAPI(UserAdminReadService userAdminReadService) {
        this.userAdminReadService = userAdminReadService;
    }

    // user-role, user-status, sort-type, limit, offset
    // user-role : null, ADMIN, USER
    // user-status : delete_flag
    // sort-type : user_seq, user_nickname, user_email
    // limit : 10, 20, 50
    // offset : 0, 10, 20, 30
    // search-type : user_nickname, user_email
    // search-value : user_nickname, user_email
    @Operation(summary = "Get user list for admin", description = "Get user list for admin")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserReadAdminResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserReadAdminResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserReadAdminResponseDTO.class)))
    })
    @PostMapping("/admin/users/get")
    public ResponseEntity<UserReadAdminResponseDTO> getUsers(
            @RequestBody UserReadAdminRequestDTO userReadAdminRequestDTO,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            CheckAdminUserUtils.isAdminOrThrowException(userDetail);
            return ResponseEntity.ok().body(userAdminReadService.getUsers(userReadAdminRequestDTO));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(403).body(
                    UserReadAdminResponseDTO.builder()
                            .error_message("You do not have permission to access this resource.")
                            .build());
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                    UserReadAdminResponseDTO.builder()
                            .error_message("An unexpected error occurred.")
                            .build());
        }
    }
}
