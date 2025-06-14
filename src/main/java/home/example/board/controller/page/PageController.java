package home.example.board.controller.page;

import home.example.board.service.search.SearchService;
import home.example.board.service.post.CheckPostService;
import home.example.board.service.post.ReadPostService;
import home.example.board.service.user.ReadUserService;
import io.swagger.v3.oas.annotations.Parameter;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class PageController {

    @Autowired
    ReadPostService readPostService;

    @Autowired
    CheckPostService checkPostService;

    @Autowired
    ReadUserService readUserService;

    @Autowired
    SearchService searchService;

    @GetMapping("/")
    public String index() {
        return "redirect:/imgArchive";
    }

    @GetMapping("/board")
    public String board(Model model) {
        model.addAttribute("subject_seq", 0);
        return "community/mainBoard";
    }

    @GetMapping("/board/{subject_seq}")
    public String boardCategory(
            @PathVariable Long subject_seq,
            Model model) {
        model.addAttribute("subject_seq", subject_seq);
        return "community/mainBoard";
    }

    @GetMapping("/write/{subject_seq}")
    public String write(
            @PathVariable long subject_seq,
            Model model)
    {
        model.addAttribute("subject_seq", subject_seq);
        return "community/writePost";
    }

    @GetMapping("/modify/{post_seq}")
    public String modify(@PathVariable long post_seq, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        long user_seq = Long.parseLong(session.getAttribute("user_seq").toString());
        if(checkPostService.getPostByUser(post_seq, user_seq)){
            model.addAttribute("post_seq", post_seq);
            return "community/modifyPost";
        }
        else{
            return "redirect:/board";
        }
    }


    @GetMapping("/post/{post_seq}")
    public String post(@PathVariable long post_seq, Model model) {
        JSONObject post = readPostService.getPost(post_seq);
        model.addAttribute("data", post);
        model.addAttribute("post_seq", post_seq);
        return "community/detailPost";
    }

    @GetMapping("/search")
    public String search(
            @Parameter(description = "검색어", required = true)
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @Parameter(description = "offset", required = true)
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @Parameter(description = "limit", required = true)
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @Parameter(description = "searchType", required = true)
            @RequestParam(value = "searchType", defaultValue = "all") String searchType,
            Model model
    ) {
        //JSONObject result = searchService.search(keyword, offset, limit, searchType);
        //model.addAttribute("data", result);
        return "community/searchResult";
    }

    @GetMapping("/imgArchive")
    public String imgArchive() {
        return "community/imgArchive";
    }
}
