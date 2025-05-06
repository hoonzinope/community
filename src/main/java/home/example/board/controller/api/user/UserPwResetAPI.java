package home.example.board.controller.api.user;

import home.example.board.service.user.UpdateUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class UserPwResetAPI {

    private final UpdateUserService updateUserService;

    @Autowired
    public UserPwResetAPI(UpdateUserService updateUserService) {
        this.updateUserService = updateUserService;
    }

    @Operation(summary = "비밀번호 초기화", description = "비밀번호를 초기화합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "비밀번호 초기화 성공",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"success\" : \"true\"}"
                                            )
                                    }
                            )
                    }),
            @ApiResponse(
                    responseCode="400",
                    description = "비밀번호 초기화 실패",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 response",
                                                    value = "{\"error\" : \"error message\"}"
                                            )
                                    }
                            )
                    })
    })
    @PatchMapping("/member/me/passwordReset")
    public ResponseEntity<JSONObject> userPasswordReset(HttpServletRequest request) {
        log.info("UserPwResetAPI - userPasswordReset");
        JSONObject jsonObject = new JSONObject();
        long user_seq = (long) request.getSession().getAttribute("user_seq");
        try{
            updateUserService.userPasswordReset(user_seq);
            jsonObject.put("success", true);
        } catch (IllegalArgumentException e) {
            log.error("update user password failed "+e.getMessage());
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().body(jsonObject);
    }

}
