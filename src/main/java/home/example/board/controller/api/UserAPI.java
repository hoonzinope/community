package home.example.board.controller.api;

import home.example.board.service.UserService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
public class UserAPI {

    @Autowired
    private UserService userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<JSONObject> addUsers(@RequestBody Map<String, Object> userInfo) {
        String user_name = (String) userInfo.get("user_name");
        String user_pw = (String) userInfo.get("user_pw");
        String user_email = (String) userInfo.get("user_email");

        try{
            userService.addUsers(user_name, user_pw, user_email);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().build();
    }
}
