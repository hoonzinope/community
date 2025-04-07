package home.example.board.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "message", required = false) String message,
            HttpServletRequest request, Model model) {

        if(error != null){
            model.addAttribute("error", error);
            model.addAttribute("message", message);
        }else{
            HttpSession session = request.getSession();
            // Referer 헤더를 통해 이전 페이지 URL 획득
            String referer = request.getHeader("Referer");
            // referer 값이 null일 경우를 대비한 기본값 설정 (예: 메인 페이지)
            if(referer == null || referer.isEmpty() || referer.contains("signup") || referer.contains("login")){
                referer = "/";
            }
            session.setAttribute("referer", referer);
        }

        return "login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }
}
