package id.my.schedule.model;

import id.my.schedule.entity.EmployeeDivision;
import id.my.schedule.entity.EmployeePosition;
import id.my.schedule.entity.UserRole;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class EmployeeResponse extends UserResponse {

    private String fullname;

    private EmployeeDivision division;

    private EmployeePosition position;

    private Integer number;

    public EmployeeResponse(String id, String nickname, UserRole role, String fullname, EmployeeDivision division, EmployeePosition position, Integer number) {
        super(id, nickname, role);
        this.division = division;
        this.fullname = fullname;
        this.position = position;
        this.number = number;
    }
}
