package id.my.schedule.service;

import id.my.schedule.model.LoginRequest;
import id.my.schedule.model.UserResponse;

import java.util.concurrent.Future;

public interface AuthService {

    Future<String> login(LoginRequest request);

    Future<Boolean> validateToken(String authToken);

    Future<UserResponse> generateUser(String token);

    Future<Boolean> checkDeveloperRole(UserResponse userResponse);
}
