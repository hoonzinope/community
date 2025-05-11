package home.example.board.controller.page;

import home.example.board.DTO.CustomUserDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping("/admin")
    public String adminPage(@AuthenticationPrincipal CustomUserDetail userDetail) {
        isAdmin(userDetail);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users")
    public String adminUsersPage(@AuthenticationPrincipal CustomUserDetail userDetail) {
        isAdmin(userDetail);
        return "admin/user/admin_user";
    }

    @GetMapping("/admin/posts")
    public String adminPostsPage(@AuthenticationPrincipal CustomUserDetail userDetail) {
        isAdmin(userDetail);
        return "admin/post/admin_post";
    }

    @GetMapping("/admin/comments")
    public String adminCommentsPage(@AuthenticationPrincipal CustomUserDetail userDetail) {
        isAdmin(userDetail);
        return "admin/comment/admin_comment";
    }

    @GetMapping("/admin/subjects")
    public String adminSubjectsPage(@AuthenticationPrincipal CustomUserDetail userDetail) {
        isAdmin(userDetail);
        return "admin/admin_subject";
    }

    @GetMapping("/admin/statistics")
    public String adminStatisticsPage(@AuthenticationPrincipal CustomUserDetail userDetail) {
        isAdmin(userDetail);
        return "admin/admin_stat";
    }

    private void isAdmin(CustomUserDetail userDetail) {
        if (userDetail == null) {
            throw new IllegalArgumentException("User detail cannot be null");
        }
        if (!userDetail.isAdmin()) {
            throw new IllegalArgumentException("User is not an admin");
        }
    }
}
