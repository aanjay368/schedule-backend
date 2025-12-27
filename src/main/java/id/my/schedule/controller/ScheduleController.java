package id.my.schedule.controller;

import id.my.schedule.model.*;
import id.my.schedule.model.schedule.ScheduleResponse;
import id.my.schedule.model.schedule.SearchScheduleResquest;
import id.my.schedule.model.schedule.UploadScheduleRequest;
import id.my.schedule.service.ScheduleService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @SneakyThrows
    @PostMapping(
            path = "/api/v1/schedules",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public WebResponse<String> upload(@RequestParam(name = "file") MultipartFile uploadFile,
                                              @RequestParam(value = "positionId") Integer positionId,
                                              @RequestParam(value = "divisionId") Integer divisionId,
                                              @RequestParam(value = "month") Integer month,
                                              @RequestParam(value = "year") Integer year) {
        UploadScheduleRequest request = UploadScheduleRequest.builder()
                .month(month)
                .year(year)
                .uploadFile(uploadFile)
                .divisionId(divisionId)
                .positionId(positionId)
                .build();

        String response = scheduleService.upload(request);
        return WebResponse.<String>builder().status(HttpStatus.OK.value()).data(response).build();
    }

    @GetMapping(
            path = "/api/v1/schedules",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ScheduleResponse>> search(
            @RequestParam(name = "date", required = false) Integer date,
            @RequestParam(name = "year") Integer year,
            @RequestParam(name = "month") Integer month,
            @RequestParam(name = "divisionId"   ) Integer divisionId,
            @RequestParam(name = "positionId"   ) Integer positionId,
            @RequestParam(name = "employeeId", required = false) String employeeId
    ) {
        SearchScheduleResquest request = SearchScheduleResquest.builder()
                .date(date)
                .month(month)
                .year(year)
                .divisionId(divisionId)
                .positionId(positionId)
                .employeeId(employeeId).build();

        List<ScheduleResponse> responses = scheduleService.search(request);
        return WebResponse.<List<ScheduleResponse>>builder().data(responses).build();
    }

    @GetMapping(
            path = "/api/v1/schedules/{scheduleId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ScheduleResponse> get(@PathVariable("scheduleId") String scheduleId) {
        ScheduleResponse response = scheduleService.getDetails(scheduleId);

        return WebResponse.<ScheduleResponse>builder().data(response).build();
    }

}
