package id.my.schedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @ManyToOne
    @JoinColumn(
            name = "color_id",
            referencedColumnName = "id"
    )
    private Color color;

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
