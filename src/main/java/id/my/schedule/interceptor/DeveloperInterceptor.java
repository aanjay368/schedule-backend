package id.my.schedule.interceptor;

import id.my.schedule.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DeveloperInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(request.getMethod().equals("GET")) {
            return true;
        }

        String token = null;

        // 1. Coba ambil dari Authorization header (untuk mobile app)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 2. Kalau tidak ada, coba ambil dari Cookie (untuk web)
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("session".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 3. Validasi token
        if (token != null && authService.validateToken(token)) {
            return authService.validateDeveloperRole(token);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or missing token");
    }
}
