package id.my.schedule.service.impl;

import id.my.schedule.entity.*;
import id.my.schedule.model.*;
import id.my.schedule.model.schedule.ScheduleResponse;
import id.my.schedule.model.schedule.SearchScheduleResquest;
import id.my.schedule.model.schedule.UploadScheduleRequest;
import id.my.schedule.repository.*;
import id.my.schedule.service.ScheduleService;
import id.my.schedule.util.CsvUtil;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private CsvUtil csvUtil;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Override
    @Transactional
    public String upload(UploadScheduleRequest request) {

        ScheduleMappingContext context = prepareMappingContext(request);

        List<CSVRecord> records;
        try {
            records = csvUtil.readRecords(request.getUploadFile());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gagal membaca file CSV.", e);
        }

        LocalDate startDate = context.getYearMonth().atDay(1);
        LocalDate endDate = context.getYearMonth().atEndOfMonth();


        // 2. HAPUS data lama agar tidak duplikat (Idempotent)
        scheduleRepository.deleteByDivisionAndPositionAndDateBetween(
                context.getDivision(),
                context.getPosition(),
                startDate,
                endDate
        );

        Set<Schedule> schedules = mapCsvToSchedules(records, context);

        scheduleRepository.saveAll(schedules);

        return String.format("Success upload data Schedule ke divisi %s dan posisi %s pada bulan %s dan tahun %s",
                context.getDivision().getName(),
                context.getPosition().getName(),
                context.getYearMonth().getMonth().getDisplayName(TextStyle.FULL, Locale.of("ID", "id")),
                context.getYearMonth().getYear()
                );
    }

    private ScheduleMappingContext prepareMappingContext(UploadScheduleRequest request) {

        Division division = divisionRepository.findById(request.getDivisionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Divisi tidak ditemukan")
        );
        Position position = positionRepository.findById(request.getPositionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Posisi tidak ditemukan")
        );

        List<Employee> employees = employeeRepository.findByDivisionAndPosition(division, position);

        List<Shift> shifts = shiftRepository.findByDivisionAndPosition(division, position);

        Map<String, Employee> employeeMap = employees.stream()
                .collect(Collectors.toMap(employee ->
                        employee.getFullname().toUpperCase(),
                        employee -> employee,
                        (existing, replacement) -> existing));

        Map<String, Shift> shiftMap = shifts.stream()
                .collect(Collectors.toMap(
                        Shift::getLabel,
                        shift -> shift,
                        (existing, replacement) -> existing));

        return new ScheduleMappingContext(
                division,
                position,
                YearMonth.of(request.getYear(), request.getMonth()),
                employeeMap,
                shiftMap
        );
    }

    private Set<Schedule> mapCsvToSchedules(List<CSVRecord> records, ScheduleMappingContext context) {

        List<String> headers = records.get(0).getParser().getHeaderNames();
        if (!headers.contains("Nama") || !headers.contains("No")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header 'Nama' atau 'No' tidak ditemukan.");
        }

        Set<Schedule> schedules = new HashSet<>();
        YearMonth yearMonth = context.getYearMonth();
        int daysInMonth = yearMonth.lengthOfMonth();

        for (CSVRecord record : records) {

            Employee employee = getEmployeeFromMap(record.get("Nama"), context.getEmployeeMap());
            updateEmployeeAbsentNumber(employee, record.get("No"));

            for (int day = 1; day <= daysInMonth; day++) {
                String header = String.valueOf(day);
                if (!headers.contains(header)) continue;

                Shift shift = getShiftFromMap(record.get(header), context.getShiftMap(), employee.getAbsentNumber(), day);

                Schedule schedule = new Schedule();
                schedule.setEmployee(employee);
                schedule.setDivision(context.getDivision());
                schedule.setPosition(context.getPosition());
                schedule.setShift(shift);
                schedule.setDate(yearMonth.atDay(day));

                schedules.add(schedule);
            }
        }
        return schedules;
    }

    private Employee getEmployeeFromMap(String fullName, Map<String, Employee> employeeMap) {
        Employee employee = employeeMap.get(fullName);
        if (employee == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Staf dengan nama '" + fullName + "' tidak ditemukan dalam konteks Divisi/Posisi yang valid."
            );
        }
        return employee;
    }

    private Shift getShiftFromMap(String shiftLabel, Map<String, Shift> shiftMap, Integer row, Integer date) {
        Shift shift = shiftMap.get(shiftLabel);
        if (shift == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Shift dengan label '" + shiftLabel + "' tidak ditemukan di Divisi yang valid pada baris " + row + " dan tanggal " + date +"."
            );
        }
        return shift;
    }

    private void updateEmployeeAbsentNumber(Employee employee, String noString) {
        try {
            employee.setAbsentNumber(Integer.parseInt(noString));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kolom 'No' harus berisi angka yang valid.",e);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> search(SearchScheduleResquest request) {

        Division division = divisionRepository.findById(request.getDivisionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Divisi tidak di temukan")
        );

        Position position = positionRepository.findById(request.getPositionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Posisi tidak ditemukan")
        );

        List<Schedule> schedules = scheduleRepository.findAll((root, query, criteriaBuilder) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("date")), request.getYear())));
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, root.get("date")), request.getMonth())));
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("division"), division)));
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("position"), position)));

            if (Objects.nonNull(request.getDate())) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(criteriaBuilder.function("DAY", Integer.class, root.get("date")), request.getDate())));
            }

            if (Objects.nonNull(request.getEmployeeId()) && !request.getEmployeeId().isBlank() && !request.getEmployeeId().isEmpty()) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.join("employee").get("id"), request.getEmployeeId())));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });

        if (schedules.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data schedule belum tersedia");
        }

        return schedules.stream()
                .sorted(Comparator.comparing(Schedule::getDate))
                .map(ScheduleResponse::toScheduleResponse)
                .toList();
    }

    @Override
    public ScheduleResponse getDetails(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data schedule tidak ditemukan")
        );
        return ScheduleResponse.toScheduleResponse(schedule);
    }


}
