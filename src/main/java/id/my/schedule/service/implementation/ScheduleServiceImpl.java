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

    @Override
    @Transactional
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
        Division division = divisionRepository.findById(request.getDivisionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Divisi tidak ditemukan"));

        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Posisi tidak ditemukan"));

        // OPTIMASI: Gunakan Range Date daripada fungsi SQL YEAR/MONTH agar Index Database terpakai
        LocalDate start = LocalDate.of(request.getYear(), request.getMonth(), 1);
        LocalDate end = YearMonth.of(request.getYear(), request.getMonth()).atEndOfMonth();

        List<Schedule> schedules = scheduleRepository.findAll((root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.between(root.get("date"), start, end));
            predicates.add(cb.equal(root.get("division"), division));
            predicates.add(cb.equal(root.get("position"), position));
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (Objects.nonNull(request.getDate())) {
                predicates.add(cb.equal(cb.function("DAY", Integer.class, root.get("date")), request.getDate()));
            }

            if (Objects.nonNull(request.getOwnerId()) && !request.getOwnerId().isBlank()) {
                predicates.add(cb.equal(root.join("owner").get("id"), request.getOwnerId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });

        if (schedules.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data schedule belum tersedia");
        }

        return schedules.stream()
                .sorted(Comparator.comparing(Schedule::getDate))
                .map(ScheduleResponse::toScheduleResponse)
                .toList();
    }

    @Transactional
    public void syncSchedules(List<CSVRecord> records, ScheduleMappingContext context) {
        LocalDate startDate = context.getYearMonth().atDay(1);
        LocalDate endDate = context.getYearMonth().atEndOfMonth();

        // 1. Ambil data lama dan buat MAP (O(1) lookup)
        List<Schedule> existingSchedules = scheduleRepository.findByDivisionAndPositionAndDateBetween(
                context.getDivision(), context.getPosition(), startDate, endDate);

        Map<String, Schedule> existingMap = existingSchedules.stream()
                .collect(Collectors.toMap(
                        s -> s.getOwner().getId() + "_" + s.getDate().toString(),
                        s -> s,
                        (a, b) -> a));

        // 2. Map CSV ke object
        Set<Schedule> incomingSchedules = mapCsvToSchedules(records, context);

        // 3. Simpan perubahaan Employee (jika ada) dalam satu batch
        employeeRepository.saveAll(context.getEmployeeMap().values());

        List<Schedule> schedulesToSave = new ArrayList<>();
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Makassar"));

        // 4. Proses Sync menggunakan Map (Sangat Cepat)
        for (Schedule incoming : incomingSchedules) {
            String key = incoming.getOwner().getId() + "_" + incoming.getDate().toString();
            Schedule existing = existingMap.get(key);

            if (existing != null) {
                existing.setShift(incoming.getShift());
                existing.setIsDeleted(false);
                schedulesToSave.add(existing);
            } else {
                schedulesToSave.add(incoming);
            }
        }

        // 5. Simpan semua dalam satu transaksi batch
        scheduleRepository.saveAll(schedulesToSave);

        // 6. Soft delete data yang tidak ada di CSV baru
        List<Schedule> toDeleteOrUpdate = existingSchedules.stream()
                .filter(s -> s.getDate().isAfter(now))
                .filter(s -> !incomingSchedules.contains(s)) // IncomingSchedules harus override equals/hashcode
                .peek(s -> {
                    if (s.getAsReceiverSubmissions().isEmpty() && s.getAsSenderSubmissions().isEmpty()) {
                        s.setIsDeleted(true); // Sebaiknya soft delete di 0.1 CPU agar tidak memicu re-indexing berat
                    } else {
                        s.setIsDeleted(true);
                    }
                }).toList();

        scheduleRepository.saveAll(toDeleteOrUpdate);
    }

    private ScheduleMappingContext prepareMappingContext(UploadScheduleRequest request) {
        Division division = divisionRepository.findById(request.getDivisionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Divisi tidak ditemukan"));
        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Posisi tidak ditemukan"));

        List<Employee> employees = employeeRepository.findByDivisionAndPositionAndStatus(division, position, EmployeeStatus.ACTIVE);
        List<Shift> shifts = shiftRepository.findByDivisionAndPosition(division, position);

        Map<String, Employee> employeeMap = employees.stream()
                .collect(Collectors.toMap(e -> e.getFullname().toUpperCase(), e -> e, (a, b) -> a));

        Map<String, Shift> shiftMap = shifts.stream()
                .collect(Collectors.toMap(Shift::getLabel, s -> s, (a, b) -> a));

        return new ScheduleMappingContext(division, position, YearMonth.of(request.getYear(), request.getMonth()), employeeMap, shiftMap);
    }

    private Set<Schedule> mapCsvToSchedules(List<CSVRecord> records, ScheduleMappingContext context) {
        // Optimasi: Hindari stream berulang kali untuk menghitung total kosong
        List<CSVRecord> validRecords = records.stream()
                .filter(r -> r.isMapped("Nama"))
                .toList();

        int totalEmptyInCsv = (int) validRecords.stream()
                .filter(r -> r.get("Nama") == null || r.get("Nama").trim().isEmpty()).count();

        List<Employee> placeholders = getOrUpdatePlaceholders(totalEmptyInCsv, context);

        Set<Schedule> schedules = new HashSet<>();
        int daysInMonth = context.getYearMonth().lengthOfMonth();
        int emptyIdx = 0;

        for (CSVRecord record : validRecords) {
            String nameInCsv = record.get("Nama");
            Employee employee = (nameInCsv == null || nameInCsv.trim().isEmpty())
                    ? placeholders.get(emptyIdx++)
                    : getEmployeeFromMap(nameInCsv, context.getEmployeeMap());

            updateEmployeeAbsentNumber(employee, record.get("No"));

            for (int day = 1; day <= daysInMonth; day++) {
                String dayStr = String.valueOf(day);
                if (!record.isMapped(dayStr)) continue;

                String shiftLabel = record.get(dayStr);
                Shift shift = getShiftFromMap(shiftLabel, context.getShiftMap(), employee.getAbsentNumber(), day);

                Schedule s = new Schedule();
                s.setOwner(employee);
                s.setFiller(employee);
                s.setDivision(context.getDivision());
                s.setPosition(context.getPosition());
                s.setShift(shift);
                s.setDate(context.getYearMonth().atDay(day));
                s.setIsDeleted(false);
                schedules.add(s);
            }
        }
        return schedules;
    }

    private List<Employee> getOrUpdatePlaceholders(int requiredCount, ScheduleMappingContext context) {
        List<Employee> existing = employeeRepository.findByDivisionAndPositionAndNickname(
                context.getDivision(), context.getPosition(), "Kosong");

        if (existing.size() < requiredCount) {
            List<Employee> newEmployees = new ArrayList<>();
            for (int i = existing.size() + 1; i <= requiredCount; i++) {
                Employee e = new Employee();
                e.setFullname("Kosong " + i); // Unikkan nama agar tidak bentrok di map
                e.setNickname("Kosong");
                e.setDivision(context.getDivision());
                e.setPosition(context.getPosition());
                e.setStatus(EmployeeStatus.ACTIVE);
                newEmployees.add(e);
            }
            existing.addAll(employeeRepository.saveAll(newEmployees)); // Batch save
        }
        existing.sort(Comparator.comparing(Employee::getFullname));
        return existing;
    }

    // Utility methods tetap sama namun dipastikan tidak melakukan query DB
    private Employee getEmployeeFromMap(String fullName, Map<String, Employee> map) {
        Employee e = map.get(fullName.toUpperCase());
        if (e == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staf '" + fullName + "' tidak ditemukan.");
        return e;
    }

    private Shift getShiftFromMap(String label, Map<String, Shift> map, Integer row, Integer day) {
        Shift s = map.get(label);
        if (s == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift '" + label + "' salah di baris " + row + " tgl " + day);
        return s;
    }

    private void updateEmployeeAbsentNumber(Employee employee, String no) {
        try { employee.setAbsentNumber(Integer.parseInt(no)); }
        catch (Exception e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No absen harus angka"); }
    }

    @Override
    public ScheduleResponse getDetails(String scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .map(ScheduleResponse::toScheduleResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data tidak ditemukan"));
    }
}