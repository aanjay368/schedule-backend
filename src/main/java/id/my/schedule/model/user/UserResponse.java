package id.my.schedule.model.user;

import id.my.schedule.entity.User;
import id.my.schedule.model.division.PositionResponse;
import id.my.schedule.model.division.DivisionResponse;
import lombok.*;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String id;

    private String username;

    private String theme;

    private DivisionResponse division;

    private PositionResponse position;

    public static UserResponse toUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .division(
                        DivisionResponse.toDivisionResponse(user.getEmployee().getDivision())
                )
                .position(
                        PositionResponse.toPositionResponse(user.getEmployee()  .getPosition())
                ).build();
    }
}
