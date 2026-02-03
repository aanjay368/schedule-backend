package id.my.schedule.model.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.my.schedule.entity.enum_entity.ShiftColor;
import id.my.schedule.validator.annotation.NotExistDivisionAndPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NotExistDivisionAndPosition
public class CreateShiftRequest {

    @NotNull(message = "Nama tidak boleh kosong")
    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(min = 1,  max = 15, message = "Panjang nama minimal 1 sampai 15 karakter")
    private String name;

    @NotNull(message = "Label tidak boleh kosong")
    @NotBlank(message = "Label tidak boleh kosong")
    @Size(min= 1, max = 2, message = "Label hanya boleh dua karakter")
    private String label;

    @NotNull(message = "Warna tidak boleh kosong")
    private ShiftColor color;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime start;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime end;

    private Integer divisionId;

    private Integer positionId;


}
