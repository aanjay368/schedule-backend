package id.my.schedule.repository;

import id.my.schedule.entity.Employee;
import id.my.schedule.entity.Schedule;
import id.my.schedule.entity.Submission;
import id.my.schedule.entity.enum_entity.SubmissionStatus;
import id.my.schedule.entity.enum_entity.SubmissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, String>, JpaSpecificationExecutor<Submission> {

    Optional<Submission> findByIdAndStatus(String submissionId, SubmissionStatus status);

    List<Submission> findAllByStatus(SubmissionStatus submissionStatus);

    List<Submission> findAllByTypeAndStatus(SubmissionType type, SubmissionStatus status);
}
