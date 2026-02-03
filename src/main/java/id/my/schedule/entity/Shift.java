package id.my.schedule.entity;

import id.my.schedule.entity.enum_entity.ShiftColor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String label;

    private LocalTime start;

    private LocalTime end;

    @Enumerated(EnumType.STRING)
    private ShiftColor color;

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

    @OneToMany(mappedBy = "shift")
    private List<Schedule> schedules;
}
