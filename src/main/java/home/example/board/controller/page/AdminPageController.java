package home.example.board.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping("/admin")
    public String adminPage() {
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users")
    public String adminUsersPage() {
        return "admin/user/admin_user";
    }

    @GetMapping("/admin/posts")
    public String adminPostsPage() {
        return "admin/post/admin_post";
    }

    @GetMapping("/admin/comments")
    public String adminCommentsPage() {
        return "admin/comment/admin_comment";
    }

    @GetMapping("/admin/subjects")
    public String adminSubjectsPage() {
        return "admin/admin_subject";
    }

    @GetMapping("/admin/statistics")
    public String adminStatisticsPage() {
        return "admin/admin_stat";
    }
}
