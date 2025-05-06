package home.example.board.controller.page;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.service.user.ReadUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserPageController {

    @Autowired
    ReadUserService readUserService;

    @GetMapping("/member/openProfile")
    public String openProfile(
            @RequestParam (value = "user_nickname", required = true, defaultValue = "0" ) String user_nickname,
            Model model) {
        if (user_nickname.equals("0")) {
            throw new IllegalArgumentException("User nickname cannot be null");
        }
        readUserService.getUserInfo(model, user_nickname);
        model.addAttribute("insert_ts", model.getAttribute("insert_ts").toString().substring(0, 10));
        return "user/openProfile";
    }

    @GetMapping("/member/closeProfile")
    public String closeProfile(
            @AuthenticationPrincipal CustomUserDetail user,
            Model model) {
        long user_seq = user.getUserSeq();
        readUserService.getUserInfo(model, user_seq);
        return "user/closeProfile";
    }

    @GetMapping("/changePassword")
    public String changePassword() {
        return "user/changePassword";
    }


}
