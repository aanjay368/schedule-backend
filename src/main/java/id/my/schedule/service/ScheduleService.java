package id.my.schedule.service;

import id.my.schedule.model.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

public interface ScheduleService {

    Future<String> upload(UploadScheduleRequest request) throws IOException;

    Future<List<MonthlyScheduleResponse>> getMonthlySchedule(MonthlyScheduleResquest request);

    Future<List<DailyScheduleResponse>> getDailySchedule(DailyScheduleRequest request);
}
