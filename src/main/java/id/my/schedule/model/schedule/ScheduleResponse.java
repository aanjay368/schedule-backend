package id.my.schedule.model.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.my.schedule.entity.Schedule;
import id.my.schedule.entity.ScheduleHistory;
import id.my.schedule.model.shift.ShiftResponse;
import id.my.schedule.model.employee.EmployeeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponse {

    private String id;

    private ShiftResponse shift;

    private String date;

    private EmployeeResponse owner;

    private EmployeeResponse filler;

    private List<String> histories;

    @JsonIgnore
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.of("ID", "id"));

    public static ScheduleResponse toScheduleResponse(Schedule schedule) {

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .shift(ShiftResponse.toShiftResponse(schedule.getShift()))
                .date(schedule.getDate().format(formatter))
                .owner(EmployeeResponse.toEmployeeResponse(schedule.getOwner()))
                .filler(Objects.nonNull(schedule.getFiller()) ? EmployeeResponse.toEmployeeResponse(schedule.getFiller()) : null)
                .histories(schedule.getHistories().stream()
                        .sorted(Comparator.comparing(ScheduleHistory::getCreatedAt))
                        .map(ScheduleHistory::getNote).toList())
                .build();

    }

}
