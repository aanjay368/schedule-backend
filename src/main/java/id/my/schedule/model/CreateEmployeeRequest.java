package id.my.schedule.model;

import id.my.schedule.validator.ExistEmployeeNickname;
import id.my.schedule.validator.NotExistEmployeeDivision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateEmployeeRequest {

    @NotBlank(message = "Nama panggilan tidak boleh kosong")
    @Size(min = 3, max = 20, message = "Nama panggilan minimal 3 Karakter")
    @ExistEmployeeNickname
    private String nickname;

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(min = 3, max  = 100, message = "Nama lengkap minimal 3 Karakter")
    private String fullname;

    @NotBlank(message = "Divisi tidak boleh kosong")
    @NotExistEmployeeDivision
    private String division;

}
