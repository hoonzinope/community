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

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        HttpSession session = request.getSession();
        // Referer 헤더를 통해 이전 페이지 URL 획득
        String referer = request.getHeader("Referer");

        // referer 값이 null일 경우를 대비한 기본값 설정 (예: 메인 페이지)
        if(referer == null || referer.isEmpty() || referer.contains("signup") || referer.contains("login")){
            referer = "/";
        }
        session.setAttribute("referer", referer);

        return "login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }
}
