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
            String referer = request.getHeader("referer");
            if (referer != null && !referer.contains("/login")) {
                request.getSession().setAttribute("referer", referer);
            } else if(session.getAttribute("referer") != null) {
                referer = (String) session.getAttribute("referer");
            } else{
                referer = "/";
            }
            session.setAttribute("referer", referer);
        }

        return "member/login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "member/signup";
    }
}
