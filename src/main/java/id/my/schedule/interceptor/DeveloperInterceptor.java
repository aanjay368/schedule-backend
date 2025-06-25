package id.my.schedule.interceptor;

import id.my.schedule.model.UserResponse;
import id.my.schedule.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class DeveloperInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getMethod().equalsIgnoreCase("GET")){
            return true;
        }

        UserResponse userResponse = (UserResponse) request.getAttribute("user");
        return authService.checkDeveloperRole(userResponse).get();
    }
}
