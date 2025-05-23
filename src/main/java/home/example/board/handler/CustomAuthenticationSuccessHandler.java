package home.example.board.handler;

import home.example.board.DTO.CustomUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SessionRegistry sessionRegistry;

    public CustomAuthenticationSuccessHandler(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        String referer = "/";
        if (session != null && session.getAttribute("referer") != null) {
            referer = session.getAttribute("referer").toString();
            session.removeAttribute("referer"); // 한 번 쓰고 지우는 게 좋음
        }
        CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
        String userNickname = userDetails.getUserNickName().split("-")[0];

        try {
            // 세션 등록
            sessionRegistry.registerNewSession(session.getId(), userDetails);

            // 기존 세션 만료
            List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
            if (sessions.size() > 1) {
                for (SessionInformation info : sessions) {
                    if (!info.getSessionId().equals(session.getId())) {
                        info.expireNow();
                    }
                }
            }

            // 세션 쿠키 명시적 설정
            Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
            // sessionCookie.setPath("/");
            // 프로덕션 환경에서는 secure 속성 적용 고려
            // sessionCookie.setSecure(true);
            // sessionCookie.setHttpOnly(true);
            response.addCookie(sessionCookie);
        } catch (Exception e) {
            logger.error("세션 처리 중 오류 발생: " + e.getMessage());
        }

        session.setAttribute("user_seq", userDetails.getUserSeq());
        session.setAttribute("user_nickname", userNickname);
        session.setAttribute("user_role", userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList()));
        if (userDetails.getTempPassword()) {
            referer = "/changePassword";
        }
        getRedirectStrategy().sendRedirect(request, response, referer);
    }
}
