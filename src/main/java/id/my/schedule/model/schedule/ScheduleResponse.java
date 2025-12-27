package id.my.schedule.model.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.my.schedule.entity.Schedule;
import id.my.schedule.model.shift.ShiftResponse;
import id.my.schedule.model.employee.EmployeeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponse {

    private String id;

    private ShiftResponse shift;

    private String date;

    private EmployeeResponse employee;

    @JsonIgnore
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.of("ID", "id"));

    public static ScheduleResponse toScheduleResponse(Schedule schedule) {

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .shift(ShiftResponse.toShiftResponse(schedule.getShift()))
                .date(schedule.getDate().format(formatter))
                .employee(EmployeeResponse.toEmployeeResponse(schedule.getEmployee()))
                .build();

    }

}
