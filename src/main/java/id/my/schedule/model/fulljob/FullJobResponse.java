package id.my.schedule.model.fulljob;

import id.my.schedule.entity.FullJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullJobResponse {

    private String id;

    private String name;

    private String date;

    private String createdAt;

    @Autowired
    private static DateTimeFormatter formatter;

    public static FullJobResponse toFullJobResponse(FullJob fullJob){
        return FullJobResponse.builder()
                .id(fullJob.getId())
                .name(fullJob.getName())
                .date(fullJob.getDate().format(formatter))
                .createdAt(fullJob.getCreatedAt().format(formatter))
                .build();
    }
}
