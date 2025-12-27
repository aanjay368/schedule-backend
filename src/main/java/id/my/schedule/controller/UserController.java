package id.my.schedule.controller;

import id.my.schedule.entity.User;
import id.my.schedule.model.user.UpdateUserRequest;
import id.my.schedule.model.user.UserResponse;
import id.my.schedule.model.WebResponse;
import id.my.schedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(
            path = "/api/v1/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> getCurrent(User user){
        UserResponse userResponse = userService.getCurrent(user);

        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(
            path = "/api/v1/users"
    )
    public WebResponse<UserResponse> update(User user,@RequestBody UpdateUserRequest request){
        request.setUser(user);
        UserResponse response = userService.update(user, request);

        return WebResponse.<UserResponse>builder().data(response).build();
    }


}
