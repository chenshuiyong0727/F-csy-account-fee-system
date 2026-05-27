package com.yj.accountfee.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaFallbackController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object pathObj = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String path = pathObj == null ? "" : pathObj.toString();
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method) && !path.startsWith("/api") && !path.contains(".")) {
            return "forward:/index.html";
        }
        return null;
    }
}
