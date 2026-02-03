package id.my.schedule.service;

import id.my.schedule.entity.BackupHistory;
import id.my.schedule.entity.Employee;
import id.my.schedule.model.backup_history.SearchBackupHistoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BackupHistoryService {

    Page<BackupHistory> search(Employee employee, SearchBackupHistoryRequest request, Pageable pageable);

}
