package id.my.schedule.model;

import id.my.schedule.entity.EmployeeDivision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyScheduleRequest {

    private LocalDate date;

    private EmployeeDivision division;

}
