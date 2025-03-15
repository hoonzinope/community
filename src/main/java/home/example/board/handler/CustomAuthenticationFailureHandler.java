package home.example.board.handler;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errorMessage;
        if (exception instanceof DisabledException) {
            // 계정이 비활성화된 경우 처리 로직 (예: 별도 에러 메시지 처리)
            System.out.println("비활성화된 계정입니다.");
            errorMessage = "inactive account";
        } else {
            // 그 외의 인증 실패에 대한 처리
            System.out.println("인증에 실패했습니다.");
            errorMessage = "authentication failed";
        }
        errorMessage = URLEncoder.encode(errorMessage, String.valueOf(StandardCharsets.UTF_8));
        response.sendRedirect("/login?error=true&message=" + errorMessage);
    }
}
