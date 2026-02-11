package id.my.schedule.model.division;

import id.my.schedule.entity.Division;
import id.my.schedule.entity.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DivisionResponse {

    private Integer id;

    private String name;

    private List<PositionResponse> positions;

    public static DivisionResponse toDivisionResponse(Division division){
        return DivisionResponse.builder()
                .id(division.getId())
                .name(division.getName())
                .positions(division.getPositions().stream()
                        .sorted(Comparator.comparing(Position::getId))
                        .map(PositionResponse::toPositionResponse).toList())
                .build();
    }
}
