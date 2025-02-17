package home.example.board.controller.api;

import home.example.board.domain.User;
import home.example.board.service.UserService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/addUser")
    public ResponseEntity<JSONObject> addUsers(@RequestBody Map<String, Object> userInfo) {
        String user_name = (String) userInfo.get("user_name");
        String user_pw = (String) userInfo.get("user_pw");
        String user_email = (String) userInfo.get("user_email");

        try{
            userService.addUsers(user_name, user_pw, user_email);
        } catch (IllegalArgumentException e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(jsonObject);
        }
        return ResponseEntity.ok().build();
    }
}
