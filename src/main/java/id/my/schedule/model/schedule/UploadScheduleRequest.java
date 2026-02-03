package id.my.schedule.model.schedule;

import id.my.schedule.validator.annotation.ResourceExists;
import id.my.schedule.repository.DivisionRepository;
import id.my.schedule.repository.PositionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class UploadScheduleRequest {

    @NotNull(message = "ID Divisi tidak boleh kosong.")
    @ResourceExists(
            repository = DivisionRepository.class,
            message = "Divisi dengan ID ini tidak ditemukan."
    )
    private Integer divisionId;

    @NotNull(message = "ID Posisi tidak boleh kosong.")
    @ResourceExists(
            repository = PositionRepository.class,
            message = "Posisi dengan ID ini tidak ditemukan."
    )
    private Integer positionId;

    @NotNull(message = "Tahun tidak boleh kosong.")
    private Integer year;

    @NotNull(message = "Bulan tidak boleh kosong.")
    private Integer month;

    @NotNull
    private MultipartFile uploadFile;

    // ... Getters dan Setters ...
}