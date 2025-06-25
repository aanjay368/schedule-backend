package id.my.schedule.service;

import id.my.schedule.entity.Employee;
import id.my.schedule.entity.User;
import id.my.schedule.model.EmployeeResponse;
import id.my.schedule.model.UserResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class UserServiceImpl implements UserService {

    @Override
    @Async
    public Future<UserResponse> getCurrent(User user) {

        return CompletableFuture.completedFuture(
                user instanceof Employee
                        ?
                        new EmployeeResponse(
                                user.getId(),
                                user.getNickname(),
                                user.getRole(),
                                ((Employee) user).getFullname(),
                                ((Employee) user).getDivision(),
                                ((Employee) user).getPosition(),
                                ((Employee) user).getNumber()
                        )
                        :
                        UserResponse.builder()
                                .id(user.getId())
                                .nickname(user.getNickname())
                                .role(user.getRole())
                                .build()
        );
    }

}
