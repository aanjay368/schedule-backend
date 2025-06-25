package id.my.schedule.model;

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

    @NotBlank(message = "Nama panggilan tidak boleh kosong")
    private String nickname;

    @NotBlank(message = "Password tidak boleh kosong")
    private String password;

}
