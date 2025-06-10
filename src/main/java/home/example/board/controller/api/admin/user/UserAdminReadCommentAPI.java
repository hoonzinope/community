package home.example.board.controller.api.admin.user;

import home.example.board.DTO.adminApiDTO.post.UserCommentReadRequestDTO;
import home.example.board.DTO.adminApiDTO.post.UserCommentReadResponseDTO;
import home.example.board.service.admin.user.UserAdminReadCommentService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserAdminReadCommentAPI {

    private final UserAdminReadCommentService userAdminReadCommentService;

    public UserAdminReadCommentAPI(UserAdminReadCommentService userAdminReadCommentService) {
        this.userAdminReadCommentService = userAdminReadCommentService;
    }
    // TODO: Implement the logic to read user comments
    @PostMapping("/admin/user/readComment")
    public ResponseEntity<UserCommentReadResponseDTO> getUserComment(
            @RequestBody UserCommentReadRequestDTO userCommentReadRequestDTO
    ) {
        log.debug("Processing getUserComment request: {}", userCommentReadRequestDTO);
        try{
            UserCommentReadResponseDTO userCommentReadResponseDTO =
                    userAdminReadCommentService.getUserComment(userCommentReadRequestDTO);
            return ResponseEntity.ok().body(userCommentReadResponseDTO);
        }catch (Exception e){
            log.error("Exception occurred while processing getUserComment request", e);
            return ResponseEntity.status(403).body(
                UserCommentReadResponseDTO.builder()
                        .error_message("exception " + e.getMessage()).build()
            );
        }
    }
}
