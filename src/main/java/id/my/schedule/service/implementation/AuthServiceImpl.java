package id.my.schedule.service.implementation;

import id.my.schedule.entity.User;
import id.my.schedule.model.auth.LoginRequest;
import id.my.schedule.model.auth.LoginResponse;
import id.my.schedule.model.user.UserResponse;
import id.my.schedule.repository.UserRepository;
import id.my.schedule.service.AuthService;
import id.my.schedule.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        validatorService.validate(request);

        User user = userRepository.findFirstByUsername(request.getUsername()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username atau password salah")
        );
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username atau password salah");
        }

        UserResponse userResponse = UserResponse.toUserResponse(user);

        String token = jwtUtil.generateToken(userResponse);
        return LoginResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }

    @Override
    public Boolean validateToken(String token) {
        if (jwtUtil.isValid(token)) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or missing token");
    }

    @Override
    public UserResponse generateUser(String token) {
        UserResponse userResponse = jwtUtil.extractUser(token);
        return userResponse;
    }

    @Override
    public boolean validateDeveloperRole(String token){
        UserResponse userResponse = generateUser(token);
        if (!userResponse.getDivision().getName().equalsIgnoreCase("IT")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbiden: Anda tidak memiliki izin akses.");
        }
        return true;
    }
}
