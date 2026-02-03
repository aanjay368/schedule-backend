package id.my.schedule.entity;

import id.my.schedule.entity.enum_entity.SubmissionStatus;
import id.my.schedule.entity.enum_entity.SubmissionType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private SubmissionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Employee sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private Employee receiver;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reference_id", referencedColumnName = "id")
    private Submission referenceSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_schedule_id", referencedColumnName = "id")
    private Schedule senderSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_schedule_id", referencedColumnName = "id")
    private Schedule receiverSchedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.PENDING;

    private String message;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(updatable = false, nullable = false)
    private LocalDateTime expiredAt;

    private LocalDateTime approvedAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime rejectedAt;
}