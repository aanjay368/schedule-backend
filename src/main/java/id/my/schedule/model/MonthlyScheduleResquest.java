package id.my.schedule.model;

import id.my.schedule.entity.EmployeeDivision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyScheduleResquest {

    private Integer month;

    private EmployeeDivision division;

    private String employeeId;
}
