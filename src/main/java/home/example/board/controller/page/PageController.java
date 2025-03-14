package home.example.board.controller.page;

import home.example.board.service.PostService;
import home.example.board.service.UserService;
import org.checkerframework.checker.units.qual.A;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class PageController {

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/board";
    }

    @GetMapping("/board")
    public String board() {
        return "index";
    }


    @GetMapping("/write")
    public String write() {
        return "write";
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Model model) {
        long user_seq = Long.parseLong(request.getSession().getAttribute("user_seq").toString());
        userService.insertUserInfo(model, user_seq);
        return "profile";
    }

    @GetMapping("/changePassword")
    public String changePassword() {
        return "changePassword";
    }

    @GetMapping("/modify/{post_seq}")
    public String modify(@PathVariable long post_seq, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        long user_seq = Long.parseLong(session.getAttribute("user_seq").toString());
        if(postService.getPostByUser(post_seq, user_seq)){
            model.addAttribute("post_seq", post_seq);
            return "modify";
        }
        else{
            return "redirect:/board";
        }
    }


    @GetMapping("/post/{post_seq}")
    public String post(@PathVariable long post_seq, Model model) {
        JSONObject post = postService.getPostView(post_seq);
        model.addAttribute("data", post);
        model.addAttribute("post_seq", post_seq);


        return "post";
    }
}
