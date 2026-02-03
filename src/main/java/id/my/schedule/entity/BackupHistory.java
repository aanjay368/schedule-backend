package id.my.schedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "backup_histories")
@EntityListeners(AuditingEntityListener.class)
public class BackupHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "submission_id")
    private String submissionId;

    // Orang yang melakukan Backup (Yang Berjasa/Bekerja)
    @Column(name = "backupper_id")
    private String backupperId;
    @Column(name = "backupper_name")
    private String backupperName;

    // Orang yang dibackup (Yang Berhutang/Punya Jadwal Asli)
    @Column(name = "debtor_id")
    private String debtorId;
    @Column(name = "debtor_name")
    private String debtorName;

    @Column(name = "work_date")
    private LocalDate workDate;
    @Column(name = "shift_label")
    private String shiftLabel;
    @Column(name = "shift_name")
    private String shiftName;

    @CreatedDate
    private LocalDateTime createdAt;
}
