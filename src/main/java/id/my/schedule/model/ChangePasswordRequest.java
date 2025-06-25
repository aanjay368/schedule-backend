package id.my.schedule.model;

import id.my.schedule.validator.ConfirmPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfirmPassword
public class ChangePasswordRequest {

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 8, max = 20, message = "panjang passwond antara 8 sampai 20 karakter")
    private String password;

    private String confirmPassword;

}
