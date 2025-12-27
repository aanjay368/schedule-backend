package id.my.schedule.model.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import id.my.schedule.repository.ColorRepository;
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
public class UpdateShiftRequest {

    @JsonIgnore
    private Integer id;

    @NotNull(message = "Nama tidak boleh kosong")
    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(min = 1,  max = 5, message = "Panjang nama minimal 1 sampai 5 karakter")
    private String name;

    @NotNull(message = "Warna tidak boleh kosong")
    @ResourceExists(repository = ColorRepository.class)
    private Integer colorId;

    @NotNull(message = "Jam dimulai tidak boleh kosong")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime start;

    @NotNull(message = "Jam berakhir tidak boleh kosong")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime end;
}
