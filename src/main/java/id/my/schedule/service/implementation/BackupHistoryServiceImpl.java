package id.my.schedule.service.implementation;

import id.my.schedule.entity.BackupHistory;
import id.my.schedule.entity.Employee;
import id.my.schedule.entity.Submission;
import id.my.schedule.entity.enum_entity.SubmissionStatus;
import id.my.schedule.entity.enum_entity.SubmissionType;
import id.my.schedule.model.backup_history.SearchBackupHistoryRequest;
import id.my.schedule.repository.BackupHistoryRespository;
import id.my.schedule.repository.ScheduleRepository;
import id.my.schedule.repository.SubmissionRepository;
import id.my.schedule.service.BackupHistoryService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class BackupHistoryServiceImpl implements BackupHistoryService {

    @Autowired
    private BackupHistoryRespository backupHistoryRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Scheduled(cron = "0 57 11 * * *")
    @Transactional
    public void processPendingBackupHistories() {

        Specification<Submission> specification= (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("type"), SubmissionType.BACKUP));

            predicates.add(root.get("status").in(SubmissionStatus.APPROVED));

            predicates.add(cb.lessThan(root.get("senderSchedule").get("date"), LocalDate.now(ZoneId.of("Asia/Makassar"))));

            return cq.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        List<Submission> approvedBackups = submissionRepository.findAll(specification);

        approvedBackups.forEach(submission -> {
            if (!backupHistoryRepository.existsBySubmissionId(submission.getId())) {
                BackupHistory backupHistory = new BackupHistory();
                
                backupHistory.setSubmissionId(submission.getId());
                backupHistory.setBackupperId(submission.getReceiver().getId());
                backupHistory.setBackupperName(submission.getReceiver().getNickname());
                backupHistory.setDebtorId(submission.getSender().getId());
                backupHistory.setDebtorName(submission.getSender().getNickname());
                backupHistory.setWorkDate(submission.getSenderSchedule().getDate());
                backupHistory.setShiftName(submission.getSenderSchedule().getShift().getName());
                backupHistory.setShiftLabel(submission.getSenderSchedule().getShift().getLabel());
                
                backupHistoryRepository.save(backupHistory);
            }
        });
        System.out.println("Backup yang tersimpan : " + approvedBackups.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BackupHistory> search(Employee employee, SearchBackupHistoryRequest request, Pageable pageable) {
        Specification<BackupHistory> specification = (root, cq,  cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            predicates.add(
                    cb.equal(root.get("backupperId"), employee.getId())
            );

            if (Objects.nonNull(request.getStartDate())) {
                predicates.add(cb.and(
                        cb.greaterThanOrEqualTo(root.get("workDate"), request.getStartDate())
                ));
            }

            if (Objects.nonNull(request.getEndDate())) {
                predicates.add(
                        cb.and(cb.lessThanOrEqualTo(root.get("workDate"), request.getEndDate()))
                );
            }

            return cq.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Page<BackupHistory> backupHistories = backupHistoryRepository.findAll(specification, pageable);

        return backupHistories;
    }

}
