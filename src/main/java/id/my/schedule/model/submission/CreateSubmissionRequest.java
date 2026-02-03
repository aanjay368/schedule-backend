package id.my.schedule.model.submission;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import id.my.schedule.entity.Employee;
import id.my.schedule.entity.enum_entity.SubmissionType;
import id.my.schedule.repository.EmployeeRepository;
import id.my.schedule.validator.annotation.ResourceExists;
import id.my.schedule.validator.annotation.ValidSubmission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ValidSubmission
public class CreateSubmissionRequest {

    @JsonIgnore
    private Employee sender;

    private SubmissionType type;

    @NotNull(message = "Penerima harus diisi")
    @NotBlank(message = "Penerima harus diisi")
    @ResourceExists(repository = EmployeeRepository.class)
    private String receiverId;

    @JsonFormat(
            pattern = "yyyy-MMMM-dd"
    )
    private LocalDate date;

    private String referenceId;

    @JsonFormat(
            pattern = "yyyy-MMMM-dd"
    )
    private LocalDate senderDate;

    @JsonFormat(
            pattern = "yyyy-MMMM-dd"
    )
    private LocalDate receiverDate;

    private String message;

}
