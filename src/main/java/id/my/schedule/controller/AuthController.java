package id.my.schedule.controller;

import id.my.schedule.model.auth.LoginRequest;
import id.my.schedule.model.auth.LoginResponse;
import id.my.schedule.model.WebResponse;
import id.my.schedule.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping(
            path = "/api/v1/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse httpResponse){
        LoginResponse response = authService.login(request);
        ResponseCookie cookie = ResponseCookie.from("session", response.getToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();

        httpResponse.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return WebResponse.<LoginResponse>builder().status(200).data(response).build();
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
