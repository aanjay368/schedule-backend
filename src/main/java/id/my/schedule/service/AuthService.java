package id.my.schedule.service;

import id.my.schedule.model.auth.LoginRequest;
import id.my.schedule.model.auth.LoginResponse;
import id.my.schedule.model.user.UserResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    Boolean validateToken(String authToken);

    UserResponse generateUser(String token);

    boolean validateDeveloperRole(String token);
}
