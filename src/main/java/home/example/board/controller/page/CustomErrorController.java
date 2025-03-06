package home.example.board.controller.page;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusObj != null) {
            int statusCode = Integer.parseInt(statusObj.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404"; // 404 에러 페이지
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500"; // 500 에러 페이지
            }
            // 기타 상태 코드에 따른 처리
        }
        return "error/default"; // 기본 에러 페이지
    }
}
