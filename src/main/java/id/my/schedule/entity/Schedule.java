package id.my.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "schedules")
@ToString
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(
            name = "employee_id",
            columnDefinition = "nickname"
    )
    private Employee employee;

    @ManyToOne
    @JoinColumn(
            name = "shift_id",
            referencedColumnName = "id"
    )
    private Shift shift;

    @ManyToOne
    @JoinColumn(
            name = "division_id",
            referencedColumnName = "id"
    )
    private Division division;

    @ManyToOne
    @JoinColumn(
            name = "position_id",
            referencedColumnName = "id"
    )
    private Position position;

}
