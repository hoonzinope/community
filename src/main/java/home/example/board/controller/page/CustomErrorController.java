package home.example.board.controller.page;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (statusObj != null) {
            status = HttpStatus.valueOf(Integer.parseInt(statusObj.toString()));
        }

        if(isApiRequest(request)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", status.value());
            response.put("error", status.getReasonPhrase());
            // 추가 정보를 담을 수 있음
            return new ResponseEntity<>(response, status);
        } else {
            if(status == HttpStatus.NOT_FOUND) {
                return "error/404"; // 404 에러 페이지
            } else if(status == HttpStatus.INTERNAL_SERVER_ERROR) {
                return "error/500"; // 500 에러 페이지
            } else{
                return "error/default"; // 기타 에러 페이지
            }
        }
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("application/json");
    }
}
