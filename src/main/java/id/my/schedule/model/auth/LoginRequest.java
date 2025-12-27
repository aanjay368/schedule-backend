package id.my.schedule.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotNull(message = "Username tidak boleh kosong")
    @NotBlank(message = "Username tidak boleh kosong")
    private String username;

    @NotNull(message = "Password tidak boleh kosong")
    @NotBlank(message = "Password tidak boleh kosong")
    private String password;

}
