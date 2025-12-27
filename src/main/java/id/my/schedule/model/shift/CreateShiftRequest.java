package id.my.schedule.model.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.my.schedule.repository.ColorRepository;
import id.my.schedule.validator.annotation.NotExistDivisionAndPosition;
import id.my.schedule.validator.annotation.ResourceExists;
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
    @Size(min = 1,  max = 11, message = "Panjang nama minimal 1 sampai 5 karakter")
    private String name;

    @NotNull(message = "Label tidak boleh kosong")
    @NotBlank(message = "Label tidak boleh kosong")
    @Size(min= 1, max = 1, message = "Label hanya boleh satu karakter")
    private String label;

    @NotNull(message = "Warna tidak boleh kosong")
    @ResourceExists(
            repository = ColorRepository.class,
            message = "Posisi dengan ID ini tidak ditemukan."
    )
    private Integer colorId;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime start;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime end;

    private Integer divisionId;

    private Integer positionId;


}
