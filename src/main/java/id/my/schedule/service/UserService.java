package id.my.schedule.service;

import id.my.schedule.entity.User;
import id.my.schedule.model.UserResponse;

import java.util.concurrent.Future;

public interface UserService {

    Future<UserResponse> getCurrent(User user);



}
