package id.my.schedule.service.implementation;

import id.my.schedule.entity.*;
import id.my.schedule.entity.enum_entity.EmployeeStatus;
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
import java.time.ZoneId;
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

    // --- 1. PUBLIC METHODS (OVERRIDE) ---

    @Override
    public String upload(UploadScheduleRequest request) {
        ScheduleMappingContext context = prepareMappingContext(request);

        List<CSVRecord> records;
        try {
            records = csvUtil.readRecords(request.getUploadFile());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gagal membaca file CSV.");
        }

        syncSchedules(records, context);

        return String.format("Success upload data Schedule ke divisi %s dan posisi %s pada bulan %s dan tahun %s",
                context.getDivision().getName(),
                context.getPosition().getName(),
                context.getYearMonth().getMonth().getDisplayName(TextStyle.FULL, Locale.of("ID", "id")),
                context.getYearMonth().getYear()
        );
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

        List<Schedule> schedules = scheduleRepository.findAll((root, cq, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.and(cb.equal(cb.function("YEAR", Integer.class, root.get("date")), request.getYear())));
            predicates.add(cb.and(cb.equal(cb.function("MONTH", Integer.class, root.get("date")), request.getMonth())));
            predicates.add(cb.and(cb.equal(root.get("division"), division)));
            predicates.add(cb.and(cb.equal(root.get("position"), position)));
            predicates.add(cb.and(cb.equal(root.get("isDeleted"), false)));

            if (Objects.nonNull(request.getDate())) {
                predicates.add(cb.and(cb.equal(cb.function("DAY", Integer.class, root.get("date")), request.getDate())));
            }

            if (Objects.nonNull(request.getOwnerId()) && !request.getOwnerId().isBlank()) {
                predicates.add(cb.and(cb.equal(root.join("owner").get("id"), request.getOwnerId())));
            }

            return cq.where(predicates.toArray(new Predicate[]{})).getRestriction();
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

    // --- 2. CORE LOGIC (SYNCHRONIZATION) ---

    @Transactional
    public void syncSchedules(List<CSVRecord> records, ScheduleMappingContext context) {
        LocalDate startDate = context.getYearMonth().atDay(1);
        LocalDate endDate = context.getYearMonth().atEndOfMonth();

        List<Schedule> existingSchedules = scheduleRepository.findByDivisionAndPositionAndDateBetween(
                context.getDivision(), context.getPosition(), startDate, endDate);

        Set<Schedule> incomingSchedules = mapCsvToSchedules(records, context);

        context.getEmployeeMap().values().forEach(employeeRepository::save);

        List<Schedule> schedulesToSave = new ArrayList<>();
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Makassar"));

        for (Schedule incoming : incomingSchedules) {

            Optional<Schedule> existingOpt = existingSchedules.stream()
                    .filter(s -> s.getOwner().getId().equals(incoming.getOwner().getId())
                            && s.getDate().equals(incoming.getDate()))
                    .findFirst();

            if (existingOpt.isPresent()) {
                Schedule existing = existingOpt.get();

                validateShiftOverlap(existing, incoming.getShift());

                existing.setShift(incoming.getShift());
                existing.setIsDeleted(false);
                schedulesToSave.add(existing);
            } else {
                schedulesToSave.add(incoming);
            }
        }

        scheduleRepository.saveAll(schedulesToSave);

        existingSchedules.stream()
                .filter(s -> s.getDate().isAfter(now))
                .filter(s -> incomingSchedules.stream()
                        .noneMatch(inc -> inc.getOwner().getId().equals(s.getOwner().getId())
                                && inc.getDate().equals(s.getDate())))
                .forEach(s -> {
                    if (s.getAsReceiverSubmissions().isEmpty() && s.getAsSenderSubmissions().isEmpty()) {
                        scheduleRepository.delete(s);
                    } else {
                        s.setIsDeleted(true);
                        scheduleRepository.save(s);
                    }
                });
    }

    private void validateShiftOverlap(Schedule existing, Shift newShift) {
        boolean hasActiveSwap = !existing.getAsSenderSubmissions().isEmpty() ||
                !existing.getAsReceiverSubmissions().isEmpty();
    }

    // --- 3. MAPPING & CONTEXT HELPERS ---

    private ScheduleMappingContext prepareMappingContext(UploadScheduleRequest request) {

        Division division = divisionRepository.findById(request.getDivisionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Divisi tidak ditemukan")
        );
        Position position = positionRepository.findById(request.getPositionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Posisi tidak ditemukan")
        );

        List<Employee> employees = employeeRepository.findByDivisionAndPositionAndStatus(division, position, EmployeeStatus.ACTIVE);

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

        int emptyRowCount = 0;
        long totalEmptyInCsv = records.stream()
                .filter(r -> r.get("Nama") == null || r.get("Nama").trim().isEmpty())
                .count();

        List<Employee> placeholders = getOrUpdatePlaceholders((int) totalEmptyInCsv, context);

        for (CSVRecord record : records) {
            String nameInCsv = record.get("Nama");
            Employee employee;

            if (nameInCsv == null || nameInCsv.trim().isEmpty()) {
                employee = placeholders.get(emptyRowCount);
                emptyRowCount++;
            } else {
                employee = getEmployeeFromMap(nameInCsv, context.getEmployeeMap());
            }

            updateEmployeeAbsentNumber(employee, record.get("No"));

            for (int day = 1; day <= daysInMonth; day++) {
                String header = String.valueOf(day);
                if (!headers.contains(header)) continue;

                Shift shift = getShiftFromMap(record.get(header), context.getShiftMap(), employee.getAbsentNumber(), day);

                Schedule schedule = new Schedule();
                schedule.setOwner(employee);
                schedule.setFiller(employee);
                schedule.setDivision(context.getDivision());
                schedule.setPosition(context.getPosition());
                schedule.setShift(shift);
                schedule.setDate(yearMonth.atDay(day));
                schedule.setIsDeleted(false);

                schedules.add(schedule);
            }
        }

        return schedules;
    }

    // --- 4. SMALL UTILITY METHODS ---

    private List<Employee> getOrUpdatePlaceholders(int requiredCount, ScheduleMappingContext context) {
        List<Employee> existingPlaceholders = employeeRepository
                .findByDivisionAndPositionAndNickname(
                        context.getDivision(), context.getPosition(), "Kosong");

        existingPlaceholders.sort(Comparator.comparing(Employee::getNickname));

        if (existingPlaceholders.size() < requiredCount) {
            int startSuffix = existingPlaceholders.size() + 1;
            for (int i = startSuffix; i <= requiredCount; i++) {
                Employee newPlaceholder = new Employee();
                newPlaceholder.setFullname("Kosong");
                newPlaceholder.setNickname("Kosong");
                newPlaceholder.setDivision(context.getDivision());
                newPlaceholder.setPosition(context.getPosition());
                newPlaceholder.setStatus(EmployeeStatus.ACTIVE);

                existingPlaceholders.add(employeeRepository.save(newPlaceholder));
            }
        }

        return existingPlaceholders;
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
}