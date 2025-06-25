package id.my.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employees")
public class Employee extends User{

    @Id
    private String id;

    private Integer number;

    private String fullname;

    @Enumerated(EnumType.STRING)
    private EmployeeDivision division;

    @Enumerated(EnumType.STRING)
    private EmployeePosition position;

}
