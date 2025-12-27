package id.my.schedule.model.employee;

import id.my.schedule.entity.Employee;
import id.my.schedule.model.division.PositionResponse;
import id.my.schedule.model.division.DivisionResponse;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeResponse {

    private String id;

    private String nickname;

    private String fullname;

    private Integer absentNumber;

    private DivisionResponse division;

    private PositionResponse position;

    public static EmployeeResponse toEmployeeResponse(Employee employee){

        return EmployeeResponse.builder()
                .id(employee.getId())
                .nickname(employee.getNickname())
                .fullname(employee.getFullname())
                .absentNumber(employee.getAbsentNumber())
                .division(
                        DivisionResponse.toDivisionResponse(employee.getDivision())
                )
                .position(
                        PositionResponse.toPositionResponse(employee.getPosition())
                ).build();

    }
}
