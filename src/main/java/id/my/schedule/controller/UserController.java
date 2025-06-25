package id.my.schedule.controller;

import id.my.schedule.entity.User;
import id.my.schedule.model.UserResponse;
import id.my.schedule.model.WebResponse;
import id.my.schedule.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @SneakyThrows
    @GetMapping(
            path = "/api/v1/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> getCurrent(User user){
        UserResponse userResponse = userService.getCurrent(user).get();
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }
}
