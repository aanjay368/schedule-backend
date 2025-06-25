package id.my.schedule.controller;

import id.my.schedule.entity.EmployeeDivision;
import id.my.schedule.model.*;
import id.my.schedule.service.ScheduleService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Month;
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
    public WebResponse<String> uploadPorterSchedule(@RequestPart(name = "file")MultipartFile file, @RequestParam(value = "division") EmployeeDivision division){
        UploadScheduleRequest request = UploadScheduleRequest.builder()
                .filename(file.getOriginalFilename())
                .inputStream(file.getInputStream())
                .division(division)
                .build();
        String response = scheduleService.upload(request).get();
        return WebResponse.<String>builder().status(HttpStatus.OK.value()).data(response).build();
    }

    @SneakyThrows
    @GetMapping(
            path = "/api/v1/schedules/monthly",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<MonthlyScheduleResponse>> getAicMonthlySchedule(
            @RequestParam(name = "month") Month month,
            @RequestParam(name = "division") EmployeeDivision division,
            @RequestParam(name = "employeeId", required = false) String employeeId
    ){

        MonthlyScheduleResquest request = MonthlyScheduleResquest.builder()
                .month(month.getValue())
                .division(division)
                .employeeId(employeeId).build();
        log.info("request : {}", request );
        List<MonthlyScheduleResponse> responses = scheduleService.getMonthlySchedule(request).get();
        return WebResponse.<List<MonthlyScheduleResponse>>builder().data(responses).build();
    }

    @SneakyThrows
    @GetMapping(
            path = "/api/v1/schedules/daily",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DailyScheduleResponse>> getDailySchedule(
            @RequestParam(name = "date") LocalDate date,
            @RequestParam(name = "division") EmployeeDivision division
    ){
        DailyScheduleRequest request = DailyScheduleRequest.builder().date(date).division(division).build();
        List<DailyScheduleResponse> responses = scheduleService.getDailySchedule(request).get();
        return WebResponse.<List<DailyScheduleResponse>>builder().data(responses).build();
    }

}
