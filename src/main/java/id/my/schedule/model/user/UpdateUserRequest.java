package id.my.schedule.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.my.schedule.entity.User;
import id.my.schedule.validator.annotation.ExistUsername;
import id.my.schedule.validator.annotation.UpdatePassword;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@UpdatePassword
public class UpdateUserRequest {

    @JsonIgnore
    private User user;

    @ExistUsername
    @Size(min = 3, max = 20, message = "Username minimal 3 Karakter")
    private String username;

    @Size(min = 8, max = 20, message = "Password lama minimal 8 Karakter")
    private String oldPassword;

    @Size(min = 8, max = 20, message = "Password baru minimal 8 Karakter")
    private String newPassword;

    @Size(min = 8, max = 20, message = "Konfirmasi Password minimal 8 Karakter")
    private String confirmPassword;

}
