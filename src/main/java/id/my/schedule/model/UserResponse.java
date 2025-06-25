package id.my.schedule.model;

import id.my.schedule.entity.UserRole;
import lombok.*;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String id;

    private String nickname;

    private UserRole role;
}
