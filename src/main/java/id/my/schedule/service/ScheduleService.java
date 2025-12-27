package id.my.schedule.service;

import id.my.schedule.model.schedule.ScheduleResponse;
import id.my.schedule.model.schedule.SearchScheduleResquest;
import id.my.schedule.model.schedule.UploadScheduleRequest;

import java.util.List;

public interface ScheduleService {

    String upload(UploadScheduleRequest request) ;

    List<ScheduleResponse> search(SearchScheduleResquest request);

    ScheduleResponse getDetails(String scheduleId);

}
