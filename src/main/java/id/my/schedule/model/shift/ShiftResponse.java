package id.my.schedule.model.shift;

import id.my.schedule.entity.Color;
import id.my.schedule.entity.Shift;
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
public class ShiftResponse implements Comparable<ShiftResponse>{

    private Integer id;

    private String name;

    private String label;

    private Color color;

    private String start;

    private String end;

    @Autowired
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public static ShiftResponse toShiftResponse(Shift shift){

        String start = shift.getStart() == null ? null : shift.getStart().format(formatter);
        String end = shift.getStart() == null ? null :  shift.getEnd().format(formatter);

        return  ShiftResponse.builder()
                .id(shift.getId())
                .name(shift.getName())
                .label(shift.getLabel())
                .start(start)
                .end(end)
                .color(shift.getColor())
                .build();
    }

    @Override
    public int compareTo(ShiftResponse shiftResponse) {
        return shiftResponse.getId();
    }
}
