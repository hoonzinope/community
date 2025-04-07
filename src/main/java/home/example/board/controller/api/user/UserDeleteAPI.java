package home.example.board.controller.api.user;

import home.example.board.service.user.RemoveUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserDeleteAPI {

    private final RemoveUserService removeUserService;

    @Autowired
    public UserDeleteAPI(RemoveUserService removeUserService) {
        this.removeUserService = removeUserService;
    }

    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "회원 탈퇴 성공",
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
                    description = "회원 탈퇴 실패",
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
    @DeleteMapping("/member/me")
    public ResponseEntity<JSONObject> deleteUser(
            HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        long user_seq = (long) request.getSession().getAttribute("user_seq");

        try{
            removeUserService.removeUser(user_seq);
            jsonObject.put("success", true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().body(jsonObject);
    }
}
