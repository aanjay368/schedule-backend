package id.my.schedule.controller;

import id.my.schedule.model.LoginRequest;
import id.my.schedule.model.WebResponse;
import id.my.schedule.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @SneakyThrows
    @PostMapping(
            path = "/api/v1/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> login(@RequestBody LoginRequest request, HttpServletResponse response){
        String token = authService.login(request).get();
        ResponseCookie cookie = ResponseCookie.from("session", token)
                .httpOnly(true)
                .secure(true) // aktifkan di HTTPS
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return WebResponse.<String>builder().status(200).data(token).build();
    }

    @DeleteMapping(
            path = "/api/v1/auth/logout"
    )
    public void logout(@CookieValue(name = "session") Cookie cookie, HttpServletResponse response){
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
