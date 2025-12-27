package id.my.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employees")
@EntityListeners(AuditingEntityListener.class)
public class Employee{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    private String nickname;

    private String fullname;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;

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

    @Column(name = "absent_number")
    private Integer absentNumber;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;


    @OneToMany(mappedBy = "employee")
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "employee")
    private List<FullJob> fullJobs;
}
