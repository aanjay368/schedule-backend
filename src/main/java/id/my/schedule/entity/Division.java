package id.my.schedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "divisions")
public class Division {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "division_position_mapping",
            joinColumns = @JoinColumn(name = "division_id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "position_id", nullable = true)
    )
    private List<Position> positions;

}
