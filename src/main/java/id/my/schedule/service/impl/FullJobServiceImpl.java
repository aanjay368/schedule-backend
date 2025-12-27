package id.my.schedule.service.impl;

import id.my.schedule.entity.FullJob;
import id.my.schedule.entity.Schedule;
import id.my.schedule.entity.User;
import id.my.schedule.model.fulljob.FullJobResponse;
import id.my.schedule.repository.FullJobRepository;
import id.my.schedule.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class FullJobServiceImpl {

    private FullJobRepository fullJobRepository;

    private ScheduleRepository scheduleRepository;

    public FullJobResponse createFullJob(User user, String scheduleId){

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule tidak ditemukan")
        );

        FullJob fullJob = new FullJob();
        fullJob.setName(schedule.getEmployee().getNickname());
        fullJob.setDate(schedule.getDate());
        fullJob.setShift(schedule.getShift().getName());
        fullJob.setEmployee(user.getEmployee());

        fullJobRepository.save(fullJob);

        return FullJobResponse.toFullJobResponse(fullJob);

    }

    public List<FullJobResponse> getFullJobList(User user){

        List<FullJob> fullJobs = user.getEmployee().getFullJobs();

        return fullJobs.stream()
                .map(FullJobResponse::toFullJobResponse)
                .toList();

    }

}
