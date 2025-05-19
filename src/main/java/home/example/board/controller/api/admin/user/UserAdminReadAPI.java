package home.example.board.controller.api.admin.user;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.DTO.adminApiDTO.UserReadAdminRequestDTO;
import home.example.board.DTO.adminApiDTO.UserReadAdminResponseDTO;
import home.example.board.DTO.adminApiDTO.UserReadOneAdminResponseDTO;
import home.example.board.service.admin.user.UserAdminReadService;
import home.example.board.utils.CheckAdminUserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "관리자용 사용자 목록 조회", description = "관리자 페이지에서 사용자 목록을 조회합니다")
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

    @Operation(summary = "관리자용 사용자 상세 조회", description = "관리자 페이지에서 사용자 상세 정보를 조회합니다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserReadOneAdminResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserReadOneAdminResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserReadOneAdminResponseDTO.class)))
    })
    @GetMapping("/admin/users/get/{user_seq}")
    public ResponseEntity<UserReadOneAdminResponseDTO> getUser(
            @PathVariable Long user_seq,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            CheckAdminUserUtils.isAdminOrThrowException(userDetail);
            return ResponseEntity.ok().body(userAdminReadService.getUser(user_seq));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(403).body(
                    UserReadOneAdminResponseDTO.builder()
                            .error_message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                    UserReadOneAdminResponseDTO.builder()
                            .error_message(e.getMessage())
                            .build());
        }
    }
}
