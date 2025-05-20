package home.example.board.controller.api.admin.user;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.DTO.adminApiDTO.UserPostReadAdminRequestDTO;
import home.example.board.DTO.adminApiDTO.UserPostReadAdminResponseDTO;
import home.example.board.service.admin.user.UserAdminReadPostService;
import home.example.board.utils.CheckAdminUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class UserAdminReadPostAPI {

    private final UserAdminReadPostService userAdminReadPostService;

    public UserAdminReadPostAPI(UserAdminReadPostService userAdminReadPostService) {
        this.userAdminReadPostService = userAdminReadPostService;
    }

    @PostMapping("/admin/user/readPost")
    public ResponseEntity<UserPostReadAdminResponseDTO> getUserPost(
            @RequestBody UserPostReadAdminRequestDTO userPostReadAdminRequestDTO,
            @AuthenticationPrincipal CustomUserDetail customUserDetail){
        try{
            log.debug("Processing getUserPost request: {}", userPostReadAdminRequestDTO);
            CheckAdminUserUtils.isAdminOrThrowException(customUserDetail);
            UserPostReadAdminResponseDTO userPostReadAdminResponseDTO =
                    userAdminReadPostService.getUserPost(userPostReadAdminRequestDTO);
            if(userPostReadAdminResponseDTO.getError_message() != null){
                return ResponseEntity.status(403).body(userPostReadAdminResponseDTO);
            } else {
                return ResponseEntity.ok().body(userPostReadAdminResponseDTO);
            }
        } catch (Exception e){
            log.error("Exception occurred", e);
            UserPostReadAdminResponseDTO resultDto = UserPostReadAdminResponseDTO.builder()
                    .error_message("exception " + e.getMessage()).build();
            return ResponseEntity.status(403).body(resultDto);
        }
    }
}
