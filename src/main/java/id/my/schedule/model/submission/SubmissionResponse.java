package id.my.schedule.model.submission;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.my.schedule.entity.Submission;
import id.my.schedule.entity.enum_entity.SubmissionStatus;
import id.my.schedule.entity.enum_entity.SubmissionType;
import id.my.schedule.model.employee.EmployeeResponse;
import id.my.schedule.model.schedule.ScheduleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

import static id.my.schedule.model.employee.EmployeeResponse.toEmployeeResponse;
import static id.my.schedule.model.schedule.ScheduleResponse.toScheduleResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionResponse {

    private String id;

    private SubmissionType type;

    private SubmissionStatus status;

    private EmployeeResponse sender;

    private EmployeeResponse receiver;

    private ScheduleResponse senderSchedule;

    private ScheduleResponse receiverSchedule;

    private SubmissionResponse reference;

    private String message;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "EEEE, dd MMMM yyyy : HH.mm",
            locale = "id",
            timezone = "Asia/Makassar"
    )
    private LocalDateTime createdAt;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "EEEE, dd MMMM yyyy : HH.mm",
            locale = "id",
            timezone = "Asia/Makassar"
    )
    private LocalDateTime expiredAt;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "EEEE, dd MMMM yyyy : HH.mm",
            locale = "id",
            timezone = "Asia/Makassar"
    )
    private LocalDateTime approvedAt;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "EEEE, dd MMMM yyyy : HH.mm",
            locale = "id",
            timezone = "Asia/Makassar"
    )
    private LocalDateTime cancelledAt;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "EEEE, dd MMMM yyyy : HH.mm",
            locale = "id",
            timezone = "Asia/Makassar"
    )
    private LocalDateTime rejectedAt;

    public static SubmissionResponse toSubmissionResponse(Submission submission) {

        return SubmissionResponse.builder()
                .id(submission.getId())
                .type(submission.getType())
                .status(submission.getStatus())
                .message(
                        Objects.nonNull(submission.getMessage()) ?
                                submission.getMessage() : "")
                .sender(toEmployeeResponse(submission.getSender()))
                .receiver(toEmployeeResponse(submission.getReceiver()))
                .senderSchedule(Objects.nonNull(submission.getSenderSchedule()) ?
                        toScheduleResponse(submission.getSenderSchedule()) : null)
                .receiverSchedule(
                        Objects.nonNull(submission.getReceiverSchedule()) ?
                                toScheduleResponse(submission.getReceiverSchedule()) : null)
                .reference(
                        Objects.nonNull(submission.getReferenceSubmission()) ?
                                toSubmissionResponse(submission.getReferenceSubmission()) : null)
                .createdAt(submission.getCreatedAt())
                .expiredAt(submission.getExpiredAt())
                .approvedAt(Objects.nonNull(submission.getApprovedAt()) ? submission.getApprovedAt() : null)
                .rejectedAt(Objects.nonNull(submission.getRejectedAt()) ? submission.getRejectedAt() : null)
                .cancelledAt(Objects.nonNull(submission.getCancelledAt()) ? submission.getCancelledAt() : null)
                .build();
    }

}
