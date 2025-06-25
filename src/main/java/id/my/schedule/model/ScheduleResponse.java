package id.my.schedule.model;

import id.my.schedule.entity.Shift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponse {

    private String id;

    private EmployeeResponse employee;

    private String filler;

    private Integer date;

    private Shift shift;

}
