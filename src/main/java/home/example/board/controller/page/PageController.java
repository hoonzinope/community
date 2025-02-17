package home.example.board.controller.page;

import home.example.board.service.PostService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @Autowired
    PostService postService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/write")
    public String write() {
        return "write";
    }

    @GetMapping("/post/{post_seq}")
    public String post(@PathVariable long post_seq, Model model) {
        JSONObject post = postService.getPostView(post_seq);
        model.addAttribute("data", post);
        model.addAttribute("post_seq", post_seq);


        return "post";
    }
}
