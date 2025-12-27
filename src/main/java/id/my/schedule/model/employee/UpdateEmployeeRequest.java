package id.my.schedule.model.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.my.schedule.validator.annotation.NotExistDivisionAndPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NotExistDivisionAndPosition
public class UpdateEmployeeRequest {

    @JsonIgnore
    private String employeeId;

    @NotNull(message = "Nama tidak boleh kosong")
    @NotBlank(message = "Nama panggilan tidak boleh kosong")
    @Size(min = 3, max = 20, message = "Nama panggilan minimal 3 Karakter")
    private String nickname;

    @NotNull(message = "Nama Lengkap tidak boleh kosong")
    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(min = 3, max  = 100, message = "Nama lengkap minimal 3 Karakter")
    private String fullname;

    private Integer divisionId;

    private Integer positionId;
}
