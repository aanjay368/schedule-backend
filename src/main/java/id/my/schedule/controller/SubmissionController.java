package id.my.schedule.controller;

import id.my.schedule.entity.Employee;
import id.my.schedule.entity.User;
import id.my.schedule.entity.enum_entity.SubmissionStatus;
import id.my.schedule.entity.enum_entity.SubmissionType;
import id.my.schedule.model.PagingResponse;
import id.my.schedule.model.WebResponse;
import id.my.schedule.model.submission.CreateSubmissionRequest;
import id.my.schedule.model.submission.SearchSubmissionRequest;
import id.my.schedule.model.submission.SubmissionResponse;
import id.my.schedule.service.SubmissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @PostMapping(
            path = "/api/v1/submissions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SubmissionResponse> create(User user,
                                          @RequestBody CreateSubmissionRequest createRequest) {

        createRequest.setSender(user.getEmployee());
        SubmissionResponse response = submissionService.create(user.getEmployee(), createRequest);

        return WebResponse.<SubmissionResponse>builder()
                .data(response)
                .build();
    }

    @GetMapping(
            path = "/api/v1/submissions",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<SubmissionResponse>> search(
            User user,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "type", required = false) SubmissionType type,
            @RequestParam(name = "status", required = false) SubmissionStatus status,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {


        SearchSubmissionRequest request = SearchSubmissionRequest.builder()
                .name(name)
                .type(type)
                .status(status)
                .page(page)
                .size(size)
                .build();

        Page<SubmissionResponse> result = submissionService.search(user.getEmployee(), request);

        return WebResponse.<List<SubmissionResponse>>builder()
                .data(result.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(result.getNumber())
                        .totalPage(result.getTotalPages())
                        .totalElement(result.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping(
            path = "/api/v1/submissions/{submissionId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SubmissionResponse> getDetails(@PathVariable(name = "submissionId") String submissionId) {
        SubmissionResponse submissionResponse = submissionService.getDetails(submissionId);
        return WebResponse.<SubmissionResponse>builder().data(submissionResponse).build();
    }

    @PostMapping("/api/v1/submissions/{submissionId}/approve")
    public WebResponse<SubmissionResponse> approve(
            @PathVariable(name = "submissionId") String submissionId,
             User user
    ) {
        SubmissionResponse submissionResponse = submissionService.approveSubmission(user.getEmployee(),submissionId);
        return WebResponse.<SubmissionResponse>builder().data(submissionResponse).build();
    }

    @PostMapping("/api/v1/submissions/{submissionId}/reject")
    public WebResponse<SubmissionResponse> reject(
            @PathVariable(name = "submissionId") String submissionId,
            User user
    ) {
        SubmissionResponse submissionResponse = submissionService.rejectSubmission(user.getEmployee(),submissionId, "");
        return WebResponse.<SubmissionResponse>builder().data(submissionResponse).build();
    }

    @PostMapping("/api/v1/submissions/{submissionId}/cancel")
    public WebResponse<SubmissionResponse> cancel(
            @PathVariable(name = "submissionId") String submissionId,
            User user
    ) {
        SubmissionResponse submissionResponse = submissionService.cancelSubmission(user.getEmployee(),submissionId, "");
        return WebResponse.<SubmissionResponse>builder().data(submissionResponse).build();
    }

}