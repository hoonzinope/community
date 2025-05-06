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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
public class UserPwChangeAPI {

    private final UpdateUserService updateUserService;

    @Autowired
    public UserPwChangeAPI(UpdateUserService updateUserService) {
        this.updateUserService = updateUserService;
    }

    @Operation(summary = "비밀번호 변경", description = "기존 비밀번호와 새로운 비밀번호를 입력받아 비밀번호를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "비밀번호 변경 성공",
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
                    description = "비밀번호 변경 실패",
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
    @PostMapping("/member/me/passwordUpdate")
    public ResponseEntity<JSONObject> userPasswordUpdate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "success",
                                            value = "{\"user_pw\": \"test\",\"new_pw\" : \"test1234!\"}"
                                    )
                            }
                    )
            )
            @RequestBody Map<String, Object> userInfo,
            HttpServletRequest request) {
        log.info("userInfo: {}", userInfo);
        JSONObject jsonObject = new JSONObject();
        long user_seq = (long) request.getSession().getAttribute("user_seq");
        String new_pw = (String) userInfo.get("new_pw");

        try{
            updateUserService.userPasswordUpdate(user_seq, new_pw);
            jsonObject.put("success", true);
        } catch (IllegalArgumentException e) {
            log.error("update user password failed "+e.getMessage());
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().body(jsonObject);
    }
}
