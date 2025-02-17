package home.example.board.controller.page;

import home.example.board.domain.User;
import home.example.board.service.UserService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/auth/login")
    public String login(@RequestParam String user_name, @RequestParam String user_pw, HttpServletRequest request) {
        User user;
        // Referer 헤더를 통해 이전 페이지 URL 획득
        String referer = request.getHeader("Referer");

        // referer 값이 null일 경우를 대비한 기본값 설정 (예: 메인 페이지)
        if(referer == null || referer.isEmpty()){
            referer = "/";
        }

        try{
            user = userService.login(user_name, user_pw);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return "redirect:" + referer;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("user_seq", user.getUser_seq());
        session.setAttribute("user_nickname", user.getUser_nickname());
        session.setMaxInactiveInterval(1800);

        // 리다이렉트
        return "redirect:" + referer;
    }

    @GetMapping("/auth/logout")
    public String logout(HttpServletRequest request) {
        String referer = "/";
        HttpSession session = request.getSession();
        if(session == null) {
            return "redirect:" + referer;
        }
        session.invalidate();
        return "redirect:" + referer;
    }
}
