package id.my.schedule.service.implementation;

import id.my.schedule.entity.User;
import id.my.schedule.model.user.UpdateUserRequest;
import id.my.schedule.model.user.UserResponse;
import id.my.schedule.repository.UserRepository;
import id.my.schedule.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidatorService validatorService;

    @Override
    public UserResponse getCurrent(User user) {
        return UserResponse.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(User user, UpdateUserRequest request) {
        validatorService.validate(request);
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername().toLowerCase());
        } else if (request.getNewPassword() != null) {
            user.setPassword(BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);
        return UserResponse.toUserResponse(user);
    }


}
