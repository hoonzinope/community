package home.example.board.controller.api;

import home.example.board.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserAPI {

    @Autowired
    private UserService userService;

    @Operation(summary = "회원가입", description = "id, pw, email을 입력받아 회원을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(
                responseCode="200",
                description = "회원가입 성공",
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
                description = "회원가입 실패",
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
    @PostMapping("/auth/signup")
    public ResponseEntity<JSONObject> addUsers(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "success",
                                            value = "{\"user_name\": \"test\",\"user_pw\" : \"test1234!\",\"user_email\" : \"test@test.com\"}"
                                    )
                            }
                    ))
            @RequestBody Map<String, Object> userInfo) {
        JSONObject jsonObject = new JSONObject();

        String user_name = (String) userInfo.get("user_name");
        String user_pw = (String) userInfo.get("user_pw");
        String user_email = (String) userInfo.get("user_email");

        try{
            userService.addUsers(user_name, user_pw, user_email);
            jsonObject.put("success", true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().body(jsonObject);
    }
}
