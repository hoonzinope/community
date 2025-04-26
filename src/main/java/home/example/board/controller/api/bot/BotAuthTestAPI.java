package home.example.board.controller.api.bot;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotAuthTestAPI {

    @GetMapping("/api/bot/auth-test")
    public ResponseEntity<String> testBotAuth(@AuthenticationPrincipal Object principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.ok("OK");
    }

}
