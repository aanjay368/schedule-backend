package id.my.schedule.service;

import id.my.schedule.entity.Employee;
import id.my.schedule.entity.Schedule;
import id.my.schedule.entity.Shift;
import id.my.schedule.model.*;
import id.my.schedule.repository.EmployeeRepository;
import id.my.schedule.repository.ScheduleRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DateTimeFormatter formatter;

    @Override
    @Async
    @Transactional
    public Future<String> upload(UploadScheduleRequest request) throws IOException {
        String[] arrfilename = request.getFilename().split("\\.");

        if (arrfilename.length > 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename not valid");
        }

        if (!arrfilename[1].equals("csv")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename not valid");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build());
            parser.getHeaderNames().forEach(header -> {
                if (!header.equalsIgnoreCase("Nama") && !header.equalsIgnoreCase("No")) {
                    LocalDate date = YearMonth.parse(arrfilename[0], DateTimeFormatter.ofPattern("MM-yyyy")).atDay(Integer.parseInt(header));
                    if (date.isAfter(LocalDate.now()) || (date.equals(LocalDate.now()) && LocalTime.now(ZoneId.of("Asia/Makassar")).getHour() < 5)) {
                        List<Schedule> schedules = scheduleRepository.findByDateAndDivision(date, request.getDivision());
                        schedules.forEach(s -> {
                            scheduleRepository.delete(s);
                        });
                    }
                }
            });
            for (CSVRecord record : parser.getRecords()) {
                Employee employee = employeeRepository.findFirstByFullnameAndDivision(record.get("Nama"), request.getDivision()).orElseThrow(
                        () -> new IllegalArgumentException("Worker with name " + record.get("Nama") + " is not found in row " + record.get("No"))
                );

                List<Schedule> schedules = new ArrayList<>();
                for (String header : parser.getHeaderNames()) {
                    if (!header.equalsIgnoreCase("Nama") && !header.equalsIgnoreCase("No")) {

                        LocalDate date = YearMonth.parse(arrfilename[0], DateTimeFormatter.ofPattern("MM-yyyy")).atDay(Integer.parseInt(header));
                        if (date.isAfter(LocalDate.now()) || (date.equals(LocalDate.now()) && LocalTime.now(ZoneId.of("Asia/Makassar")).getHour() < 5)) {
                            Schedule schedule = new Schedule();
                            schedule.setDate(date);
                            schedule.setShift(Shift.safeValueOf(record.get(header).replace(" ", "")));
                            schedule.setEmployee(employee);
                            schedule.setDivision(request.getDivision());
                            schedules.add(schedule);
                        }
                    }

                }
                scheduleRepository.saveAll(schedules);
                employee.setNumber(Integer.parseInt(record.get("No")));
                employeeRepository.save(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return CompletableFuture.completedFuture("Success upload Schedule");
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    public Future<List<MonthlyScheduleResponse>> getMonthlySchedule(MonthlyScheduleResquest request) {

        List<Schedule> schedules = scheduleRepository.findAll((root, query, criteriaBuilder) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, root.get("date")), request.getMonth())));
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("division"), request.getDivision())));

            if (!Objects.equals(request.getEmployeeId(), "null")){
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.join("employee").get("id"), request.getEmployeeId())));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });
        if (schedules.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No schedule data available");
        }
        Map<Employee, List<Schedule>> employeeListMap = schedules.stream().collect(
                Collectors.groupingBy(Schedule::getEmployee)
        );

        List<MonthlyScheduleResponse> responses = employeeListMap.entrySet().stream()
                .map(entry ->
                        MonthlyScheduleResponse.builder()
                                .number(entry.getKey().getNumber())
                                .nickname(
                                        entry.getKey().getNickname())
                                .details(
                                        entry.getValue().stream()
                                                .sorted((Comparator.comparing(Schedule::getDate)))
                                                .map(this::toScheduleResponse).toList())
                                .build()
                )
                .sorted((Comparator.comparingInt(MonthlyScheduleResponse::getNumber)))
                .toList();
        return CompletableFuture.completedFuture(responses);

    }

    @Override
    @Async
    @Transactional(readOnly = true)
    public Future<List<DailyScheduleResponse>> getDailySchedule(DailyScheduleRequest request) {
        List<Schedule> schedules = scheduleRepository.findByDateAndDivision(request.getDate(), request.getDivision());
        
        if (schedules.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jadwal kerja belum tersedia");
        }
        
        List<DailyScheduleResponse> responses = schedules.stream().collect(
                        Collectors.groupingBy(Schedule::getShift)
                )
                .entrySet()
                .stream()
                .map(entry ->
                    DailyScheduleResponse.builder()
                            .shift(entry.getKey())
                            .schedules(
                                    entry.getValue().stream()
                                            .sorted((schedule, t1) -> schedule.getEmployee().getNumber() > t1.getEmployee().getNumber() ? schedule.getEmployee().getNumber() :t1.getEmployee().getNumber())
                                            .map(this::toScheduleResponse)
                                            .toList()
                            ).build()
                )
                .sorted(Comparator.comparing(DailyScheduleResponse::getShift))
                .toList();

        return CompletableFuture.completedFuture(responses);
    }

    private ScheduleResponse toScheduleResponse(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .date(schedule.getDate().getDayOfMonth())
                .employee(new EmployeeResponse(
                        schedule.getEmployee().getId(),
                        schedule.getEmployee().getNickname(),
                        schedule.getEmployee().getRole(),
                        schedule.getEmployee().getFullname(),
                        schedule.getEmployee().getDivision(),
                        schedule.getEmployee().getPosition(),
                        schedule.getEmployee().getNumber()
                ))
                .shift(schedule.getShift())
                .build();
    }

}
