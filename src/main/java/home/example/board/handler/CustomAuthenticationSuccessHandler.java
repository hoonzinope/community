package home.example.board.handler;

import home.example.board.DTO.CustomUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Referer 헤더를 통해 이전 페이지 URL 획득
        String referer = request.getSession().getAttribute("referer").toString();

        // referer 값이 null일 경우를 대비한 기본값 설정 (예: 메인 페이지)
        if(referer == null || referer.isEmpty()){
            referer = "/";
        }
        CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
        String user_nickname = userDetails.getUserNickName().split("-")[0];
        HttpSession session = request.getSession(true);
        session.setAttribute("user_seq", userDetails.getUserSeq());
        session.setAttribute("user_nickname", user_nickname);

        response.sendRedirect(referer);
    }
}
