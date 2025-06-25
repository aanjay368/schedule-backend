package id.my.schedule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyScheduleResponse {

    private Integer number;

    private String nickname;

    private List<ScheduleResponse> details;
}
