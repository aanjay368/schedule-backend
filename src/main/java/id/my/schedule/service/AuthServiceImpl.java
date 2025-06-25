package id.my.schedule.service;

import id.my.schedule.entity.User;
import id.my.schedule.entity.UserRole;
import id.my.schedule.model.LoginRequest;
import id.my.schedule.model.UserResponse;
import id.my.schedule.repository.UserRepository;
import id.my.schedule.security.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Async
    @Transactional
    public Future<String> login(LoginRequest request) {
        validatorService.validate(request);

        User user = userRepository.findFirstByNickname(request.getNickname()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama panggilan atau password salah")
        );
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama panggilan atau password salah");
        }

        String token = jwtUtil.generateToken(
                UserResponse.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .role(user.getRole())
                        .build()
        );
        return CompletableFuture.completedFuture(token);
    }

    @Override
    @Async
    public Future<Boolean> validateToken(String token) {
        if (jwtUtil.isValid(token)) {
            return CompletableFuture.completedFuture(true);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or missing token");
    }

    public Future<UserResponse> generateUser(String token) {
        UserResponse userResponse = jwtUtil.extractUser(token);
        return CompletableFuture.completedFuture(userResponse);
    }

    @Override
    @Async
    public Future<Boolean> checkDeveloperRole(UserResponse userResponse){
        if (!userResponse.getRole().equals(UserRole.DEVELOPER)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Umathorized: Kamu tidak punya atas URL ini");
        }
        return CompletableFuture.completedFuture(true);
    }
}
