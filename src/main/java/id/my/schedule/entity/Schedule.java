package id.my.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(
            name = "owner_id",
            columnDefinition = "id"
    )
    private Employee owner;

    @ManyToOne
    @JoinColumn(
            name = "filler_id",
            columnDefinition = "id"
    )
    private Employee filler;

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

    @OneToMany(
            mappedBy = "schedule",
            cascade = CascadeType.ALL
    )
    private List<ScheduleHistory> histories;

    @OneToMany(mappedBy = "senderSchedule")
    private List<Submission> asSenderSubmissions;

    @OneToMany(mappedBy = "receiverSchedule")
    private List<Submission> asReceiverSubmissions;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public void addHistory(String note) {
        ScheduleHistory history = new ScheduleHistory();
        history.setSchedule(this);
        history.setNote(note);
        this.histories.add(history);
    }
}
