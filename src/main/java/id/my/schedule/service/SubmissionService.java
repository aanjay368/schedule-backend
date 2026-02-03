package id.my.schedule.service;

import id.my.schedule.entity.Employee;
import id.my.schedule.model.submission.CreateSubmissionRequest;
import id.my.schedule.model.submission.SearchSubmissionRequest;
import id.my.schedule.model.submission.SubmissionResponse;
import org.springframework.data.domain.Page;


public interface SubmissionService {

    SubmissionResponse create(Employee sender, CreateSubmissionRequest request);

    Page<SubmissionResponse> search(Employee currentUser, SearchSubmissionRequest request);

    SubmissionResponse getDetails(String submissionId);

    SubmissionResponse approveSubmission(Employee receiver, String submissionId);

    SubmissionResponse rejectSubmission(Employee receiver, String submissionId, String message);

    SubmissionResponse cancelSubmission(Employee employee, String submissionId, String message);

}
