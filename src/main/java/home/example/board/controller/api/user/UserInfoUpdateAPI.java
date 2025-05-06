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
public class UserInfoUpdateAPI {

    private final UpdateUserService updateUserService;

    @Autowired
    public UserInfoUpdateAPI(UpdateUserService updateUserService) {
        this.updateUserService = updateUserService;
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "사용자 정보 수정 성공",
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
                    description = "사용자 정보 수정 실패",
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
    @PostMapping("/member/me/update")
    public ResponseEntity<JSONObject> updateUserInfo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "success",
                                            value = "{\"user_nickname\": \"test\",\"user_email\" : \"user_email\"}"
                                    )
                            }
                    )
            )
            @RequestBody Map<String, Object> userInfo,
            HttpServletRequest request) {
        log.info("userInfo: {}", userInfo);
        JSONObject jsonObject = new JSONObject();
        long user_seq = (long) request.getSession().getAttribute("user_seq");
        String user_nickname = (String) userInfo.get("user_nickname");
        String user_email = (String) userInfo.get("user_email");
        try{
            updateUserService.updateUserInfo(user_seq, user_nickname, user_email);
            request.getSession().setAttribute("user_nickname", user_nickname);
            jsonObject.put("success", true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log.error("update user info failed "+e.getMessage());
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().body(jsonObject);
    }
}
