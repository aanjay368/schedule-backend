package id.my.schedule.model;

import id.my.schedule.entity.Shift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyScheduleResponse {

    private Shift shift;

    private List<ScheduleResponse> schedules;

}
