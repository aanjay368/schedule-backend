package id.my.schedule.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditEmployeeRequest {

    @JsonIgnore
    private String id;

    private String nickname;

    private String fullname;

    private String position;

    private String division;
}
