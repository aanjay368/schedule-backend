package id.my.schedule.model.submission;

import id.my.schedule.entity.enum_entity.SubmissionStatus;
import id.my.schedule.entity.enum_entity.SubmissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchSubmissionRequest {
    private String employeeId;
    private String name;
    private SubmissionType type;
    private SubmissionStatus status;
    private Integer page;
    private Integer size;
}




