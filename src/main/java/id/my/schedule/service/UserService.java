package id.my.schedule.service;

import id.my.schedule.entity.User;
import id.my.schedule.model.user.UpdateUserRequest;
import id.my.schedule.model.user.UserResponse;

public interface UserService {

    UserResponse getCurrent(User user);

    UserResponse update(User user, UpdateUserRequest request);

}
