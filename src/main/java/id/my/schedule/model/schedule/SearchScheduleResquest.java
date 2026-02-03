package id.my.schedule.model.schedule;

import lombok.*;

@Builder
@Getter
public class SearchScheduleResquest {

    private Integer date;

    private Integer month;

    private Integer year;

    private Integer divisionId;

    private Integer positionId;

    private String ownerId;
}
