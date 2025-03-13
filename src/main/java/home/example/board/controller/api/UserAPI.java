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

import javax.servlet.http.HttpServletRequest;
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
        JSONObject jsonObject = new JSONObject();
        long user_seq = (long) request.getSession().getAttribute("user_seq");
        String user_nickname = (String) userInfo.get("user_nickname");
        String user_email = (String) userInfo.get("user_email");
        System.out.println(user_nickname + " "+ user_email);
        try{
            userService.updateUserInfo(user_seq, user_nickname, user_email);
            request.getSession().setAttribute("user_nickname", user_nickname);
            jsonObject.put("success", true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().body(jsonObject);
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
        JSONObject jsonObject = new JSONObject();
        long user_seq = (long) request.getSession().getAttribute("user_seq");
        try{
            userService.userPasswordReset(user_seq);
            jsonObject.put("success", true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().body(jsonObject);
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
        JSONObject jsonObject = new JSONObject();
        long user_seq = (long) request.getSession().getAttribute("user_seq");
        String new_pw = (String) userInfo.get("new_pw");

        try{
            userService.userPasswordUpdate(user_seq, new_pw);
            jsonObject.put("success", true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().body(jsonObject);
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
            userService.userDelete(user_seq);
            jsonObject.put("success", true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().body(jsonObject);
    }
}
