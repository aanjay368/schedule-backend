package id.my.schedule.repository;

import id.my.schedule.entity.BackupHistory;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BackupHistoryRespository extends JpaRepository<BackupHistory, String>, JpaSpecificationExecutor<BackupHistory> {

    boolean existsBySubmissionId(String submissionId);
}
