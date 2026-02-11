package id.my.schedule.service.implementation;

import id.my.schedule.entity.Employee;
import id.my.schedule.entity.Schedule;
import id.my.schedule.entity.Submission;
import id.my.schedule.entity.enum_entity.EmployeeStatus;
import id.my.schedule.entity.enum_entity.SubmissionStatus;
import id.my.schedule.entity.enum_entity.SubmissionType;
import id.my.schedule.model.submission.CreateSubmissionRequest;
import id.my.schedule.model.submission.SearchSubmissionRequest;
import id.my.schedule.model.submission.SubmissionResponse;
import id.my.schedule.repository.EmployeeRepository;
import id.my.schedule.repository.ScheduleRepository;
import id.my.schedule.repository.SubmissionRepository;
import id.my.schedule.service.SubmissionService;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private ValidatorService validatorService;

    // ==========================================
    // OVERRIDE METHODS (PUBLIC API)
    // ==========================================

    @Override
    @Transactional
    public SubmissionResponse create(Employee sender, CreateSubmissionRequest request) {
        validatorService.validate(request);

        return switch (request.getType()) {
            case SHIFT_SWAP -> handleShiftSwap(sender, request);
            case OFF_SWAP -> handleOffSwap(sender, request);
            case BACKUP -> handleBackup(sender, request);
            case CANCELLATION ->
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gunakan fitur pembatalan untuk tipe ini");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubmissionResponse> search(Employee currentUser, SearchSubmissionRequest request) {
        Specification<Submission> specification = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.or(
                    cb.equal(root.get("sender"), currentUser),
                    cb.equal(root.get("receiver"), currentUser)
            ));

            if (request.getName() != null) {
                predicates.add(cb.or(
                        cb.like(root.get("sender").get("nickname"), "%" + request.getName() + "%"),
                        cb.like(root.get("receiver").get("nickname"), "%" + request.getName() + "%"),
                        cb.like(root.get("sender").get("fullname"), "%" + request.getName() + "%"),
                        cb.like(root.get("receiver").get("fullname"), "%" + request.getName() + "%")
                ));
            }

            if (request.getType() != null) predicates.add(cb.equal(root.get("type"), request.getType()));
            if (request.getStatus() != null) predicates.add(cb.equal(root.get("status"), request.getStatus()));

            return cq.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 10,
                Sort.by("createdAt").descending()
        );

        Page<Submission> submissions = submissionRepository.findAll(specification, pageable);
        if (submissions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Belum ada Aktivitas Permintaan");
        }
        return submissions.map(SubmissionResponse::toSubmissionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getDetails(String submissionId) {
        return submissionRepository.findById(submissionId)
                .map(SubmissionResponse::toSubmissionResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data permintaan tidak ditemukan"));
    }

    @Override
    @Transactional
    public SubmissionResponse approveSubmission(Employee receiver, String submissionId) {
        Submission sub = submissionRepository.findByIdAndStatus(submissionId, SubmissionStatus.PENDING)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permintaan tidak ditemukan"));

        if (!sub.getReceiver().equals(receiver)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Anda tidak memiliki akses");
        }

        sub.setStatus(SubmissionStatus.APPROVED);
        sub.setApprovedAt(LocalDateTime.now(ZoneId.of("Asia/Makassar")));
        applyScheduleChange(sub);

        rejectAllOtherSubmissionsOnSameDate(sub);

        return SubmissionResponse.toSubmissionResponse(submissionRepository.save(sub));
    }

    @Override
    @Transactional
    public SubmissionResponse rejectSubmission(Employee receiver, String submissionId, String message) {
        Submission sub = submissionRepository.findByIdAndStatus(submissionId, SubmissionStatus.PENDING)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permintaan tidak ditemukan"));

        if (!sub.getReceiver().equals(receiver)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bukan penerima permintaan");
        }

        sub.setStatus(SubmissionStatus.REJECTED);
        sub.setRejectedAt(LocalDateTime.now(ZoneId.of("Asia/Makassar")));
        return SubmissionResponse.toSubmissionResponse(submissionRepository.save(sub));
    }

    @Override
    @Transactional
    public SubmissionResponse cancelSubmission(Employee employee, String submissionId, String message) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permintaan tidak ditemukan"));

        if (LocalDateTime.now(ZoneId.of("Asia/Makassar")).isBefore(submission.getExpiredAt())) {
            if (submission.getStatus().equals(SubmissionStatus.PENDING) && submission.getSender().equals(employee)) {
                submission.setStatus(SubmissionStatus.CANCELLED);
                submission.setCancelledAt(LocalDateTime.now(ZoneId.of("Asia/Makassar")));
                return SubmissionResponse.toSubmissionResponse(submissionRepository.save(submission));
            }

            if (submission.getStatus() == SubmissionStatus.APPROVED && (submission.getReceiver().equals(employee) || submission.getSender().equals(employee))) {
                Submission cancellationSub = new Submission();
                cancellationSub.setType(SubmissionType.CANCELLATION);
                cancellationSub.setStatus(SubmissionStatus.PENDING);
                cancellationSub.setSender(employee);
                cancellationSub.setReceiver(employee.equals(submission.getReceiver()) ? submission.getSender() : submission.getReceiver());
                cancellationSub.setMessage(employee.getNickname() + " ingin membatalkan permintaan ini");
                cancellationSub.setExpiredAt(submission.getExpiredAt());
                cancellationSub.setReferenceSubmission(submission);

                submissionRepository.save(cancellationSub);
                return SubmissionResponse.toSubmissionResponse(submission);
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Maaf, Kamu bukan pengirim maupun penerima dari permitaan ini");
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permintaan sudah tidak dapat dibatalkan");
    }

    private SubmissionResponse handleShiftSwap(Employee sender, CreateSubmissionRequest request) {
        Employee receiver = fetchEmployee(request.getReceiverId());
        Schedule receiverSchedule = fetchSchedule(receiver, request.getDate(), false);
        Schedule senderSchedule = fetchSchedule(sender, request.getDate(), false);

        return saveSubmission(request, sender, receiver, senderSchedule, receiverSchedule);
    }

    private SubmissionResponse handleOffSwap(Employee sender, CreateSubmissionRequest request) {
        Employee receiver = fetchEmployee(request.getReceiverId());
        Schedule receiverSchedule = fetchSchedule(receiver, request.getReceiverDate(), true);
        Schedule senderSchedule = fetchSchedule(sender, request.getSenderDate(), true);

        return saveSubmission(request, sender, receiver, senderSchedule, receiverSchedule);
    }

    private SubmissionResponse handleBackup(Employee sender, CreateSubmissionRequest request) {
        Schedule senderSchedule = fetchSchedule(sender, request.getDate(), false);
        Employee receiver = request.getReceiverId() != null ? fetchEmployee(request.getReceiverId()) : null;

        return saveSubmission(request, sender, receiver, senderSchedule, null);
    }

    @Transactional
    public void applyScheduleChange(Submission sub) {
        Schedule senderSched = sub.getSenderSchedule();
        Schedule receiverSched = sub.getReceiverSchedule();

        switch (sub.getType()) {
            case SHIFT_SWAP -> {
                senderSched.setFiller(sub.getReceiver());
                receiverSched.setFiller(sub.getSender());
                senderSched.addHistory(String.format("%s tukar shift dengan %s", senderSched.getFiller().getNickname(), receiverSched.getFiller().getNickname()));
                receiverSched.addHistory(String.format("%s tukar shift dengan %s", receiverSched.getFiller().getNickname(), senderSched.getFiller().getNickname()));
                scheduleRepository.save(senderSched);
                scheduleRepository.save(receiverSched);
            }
            case OFF_SWAP -> {
                senderSched.setFiller(sub.getReceiver());
                receiverSched.setFiller(sub.getSender());

                Schedule senderWorkDate = fetchSchedule(sub.getSender(), receiverSched.getDate(), false);
                Schedule receiverWorkDate = fetchSchedule(sub.getReceiver(), senderSched.getDate(), false);

                senderWorkDate.setFiller(sub.getReceiver());
                receiverWorkDate.setFiller(sub.getSender());

                senderWorkDate.addHistory(String.format("%s tukar libur dengan %s", sub.getSender().getNickname(), sub.getReceiver().getNickname()));
                receiverWorkDate.addHistory(String.format("%s tukar libur dengan %s", sub.getReceiver().getNickname(), sub.getSender().getNickname()));

                senderSched.addHistory(String.format("%s tukar libur dengan %s", sub.getSender().getNickname(), sub.getReceiver().getNickname()));
                receiverSched.addHistory(String.format("%s tukar libur dengan %s", sub.getReceiver().getNickname(), sub.getSender().getNickname()));
                scheduleRepository.save(senderSched);
                scheduleRepository.save(receiverSched);
            }
            case BACKUP -> {
                senderSched.setFiller(sub.getReceiver());
                senderSched.addHistory(String.format("%s ngefull %s", sub.getReceiver().getNickname(), sub.getSender().getNickname()));
                scheduleRepository.save(senderSched);
            }
            case CANCELLATION -> {
                Submission ref = sub.getReferenceSubmission();
                validateCancellationChain(ref);
                switch (ref.getType()) {
                    case SHIFT_SWAP -> {
                        ref.getSenderSchedule().setFiller(ref.getSender());
                        ref.getReceiverSchedule().setFiller(ref.getReceiver());
                        ref.getSenderSchedule().addHistory(String.format("%s membatalkan perubahan tukar shift sebelumnya", sub.getSender().getNickname()));
                        ref.getReceiverSchedule().addHistory(String.format("%s membatalkan perubahan tukar shift sebelumnya", sub.getSender().getNickname()));
                        scheduleRepository.save(ref.getSenderSchedule());
                        scheduleRepository.save(ref.getReceiverSchedule());
                    }
                    case OFF_SWAP -> {
                        ref.getSenderSchedule().setFiller(ref.getSender());
                        ref.getReceiverSchedule().setFiller(ref.getReceiver());

                        Schedule senderWorkDate = fetchSchedule(ref.getSender(), ref.getSenderSchedule().getDate(), false);
                        Schedule receiverWorkDate = fetchSchedule(ref.getReceiver(), ref.getReceiverSchedule().getDate(), false);

                        senderWorkDate.setFiller(ref.getReceiver());
                        receiverWorkDate.setFiller(ref.getSender());

                        senderWorkDate.addHistory(String.format("%s membatalkan perubahan tukar libur sebelumnya", sub.getSender().getNickname()));
                        receiverWorkDate.addHistory(String.format("%s membatalkan perubahan tukar shift sebelumnya", sub.getSender().getNickname()));

                        ref.getSenderSchedule().addHistory(String.format("%s membatalkan perubahan tukar libur sebelumnya", sub.getSender().getNickname()));
                        ref.getReceiverSchedule().addHistory(String.format("%s membatalkan perubahan tukar libur sebelumnya", sub.getSender().getNickname()));
                        scheduleRepository.save(ref.getSenderSchedule());
                        scheduleRepository.save(ref.getReceiverSchedule());
                    }
                    case BACKUP -> {
                        ref.getSenderSchedule().setFiller(ref.getSender());
                        ref.getSenderSchedule().addHistory(String.format("%s membatalkan permitaan ngefull sebelumnya", sub.getSender().getNickname()));
                        scheduleRepository.save(ref.getSenderSchedule());
                        scheduleRepository.save(ref.getSenderSchedule());
                    }
                }
                ref.setStatus(SubmissionStatus.CANCELLED);
                ref.setCancelledAt(LocalDateTime.now(ZoneId.of("Asia/Makassar")));
                submissionRepository.save(ref);
            }
        }
    }

    @Transactional
    public void rejectAllOtherSubmissionsOnSameDate(Submission approvedSub) {

        Specification<Submission> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.notEqual(root.get("id"), approvedSub.getId()));

            predicates.add(
                    cb.and(
                            cb.equal(root.get("status"), SubmissionStatus.PENDING)
                    )
            );

            predicates.add(
                    cb.or(
                            cb.equal(root.join("sender", JoinType.LEFT), approvedSub.getSender()),
                            cb.equal(root.join("sender", JoinType.LEFT), approvedSub.getReceiver()),
                            cb.equal(root.join("receiver", JoinType.LEFT), approvedSub.getSender()),
                            cb.equal(root.join("receiver", JoinType.LEFT), approvedSub.getReceiver())
                    )
            );

            if (approvedSub.getType().equals(SubmissionType.CANCELLATION)) {
                predicates.add(
                        cb.or(
                                cb.equal(root.join("senderSchedule", JoinType.LEFT).get("date"), approvedSub.getReferenceSubmission().getSenderSchedule().getDate()),
                                cb.equal(root.join("receiverSchedule", JoinType.LEFT).get("date"), approvedSub.getReferenceSubmission().getSenderSchedule().getDate()),
                                cb.equal(root.join("referenceSubmission", JoinType.LEFT).join("senderSchedule", JoinType.LEFT).get("date"), approvedSub.getReferenceSubmission().getSenderSchedule().getDate()),
                                cb.equal(root.join("referenceSubmission", JoinType.LEFT).join("receiverSchedule", JoinType.LEFT).get("date"), approvedSub.getReferenceSubmission().getSenderSchedule().getDate())
                        )

                );

                if (Objects.nonNull(approvedSub.getReferenceSubmission().getReceiverSchedule())) {
                    predicates.add(
                            cb.or(
                                    cb.equal(root.join("senderSchedule", JoinType.LEFT).get("date"), approvedSub.getReferenceSubmission().getReceiverSchedule().getDate()),
                                    cb.equal(root.join("receiverSchedule", JoinType.LEFT).get("date"), approvedSub.getReferenceSubmission().getReceiverSchedule().getDate()),
                                    cb.equal(root.join("referenceSubmission", JoinType.LEFT).join("senderSchedule", JoinType.LEFT).get("date"), approvedSub.getReferenceSubmission().getReceiverSchedule().getDate()),
                                    cb.equal(root.join("referenceSubmission", JoinType.LEFT).join("receiverSchedule", JoinType.LEFT).get("date"), approvedSub.getReferenceSubmission().getReceiverSchedule().getDate())
                            )
                    );
                }
            } else {
                predicates.add(
                        cb.or(
                                cb.equal(root.join("senderSchedule", JoinType.LEFT).get("date"), approvedSub.getSenderSchedule().getDate()),
                                cb.equal(root.join("receiverSchedule", JoinType.LEFT).get("date"), approvedSub.getSenderSchedule().getDate()),
                                cb.equal(root.join("referenceSubmission", JoinType.LEFT).join("senderSchedule", JoinType.LEFT).get("date"), approvedSub.getSenderSchedule().getDate()),
                                cb.equal(root.join("referenceSubmission", JoinType.LEFT).join("receiverSchedule", JoinType.LEFT).get("date"), approvedSub.getSenderSchedule().getDate())
                        )
                );
                if (Objects.nonNull(approvedSub.getReceiverSchedule())) {
                    predicates.add(

                            cb.or(
                                    cb.equal(root.join("senderSchedule", JoinType.LEFT).get("date"), approvedSub.getReceiverSchedule().getDate()),
                                    cb.equal(root.join("receiverSchedule", JoinType.LEFT).get("date"), approvedSub.getReceiverSchedule().getDate()),
                                    cb.equal(root.join("referenceSubmission", JoinType.LEFT).join("senderSchedule", JoinType.LEFT).get("date"), approvedSub.getReceiverSchedule().getDate()),
                                    cb.equal(root.join("referenceSubmission", JoinType.LEFT).join("receiverSchedule", JoinType.LEFT).get("date"), approvedSub.getReceiverSchedule().getDate())
                            )

                    );
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Submission> others = submissionRepository.findAll(spec);

        others.forEach(s -> {

            if (s.getSender().equals(approvedSub.getSender()) || s.getSender().equals(approvedSub.getReceiver())) {
                s.setStatus(SubmissionStatus.CANCELLED);
                s.setCancelledAt(LocalDateTime.now(ZoneId.of("Asia/Makassar")));
                s.setMessage("Otomatis dibatalkan: Salah satu rekan sudah menyetujui permintaan pada tanggal yang sama.");
            } else {
                s.setStatus(SubmissionStatus.REJECTED);
                s.setRejectedAt(LocalDateTime.now(ZoneId.of("Asia/Makassar")));
                s.setMessage("Otomatis ditolak: Salah satu rekan sudah menyetujui permintaan pada tanggal yang sama.");
            }

        });

        if (!others.isEmpty()) {
            submissionRepository.saveAll(others);
        }
    }

    @Transactional
    public SubmissionResponse saveSubmission(CreateSubmissionRequest request, Employee sender, Employee receiver,
                                             Schedule sSched, Schedule rSched) {
        Submission submission = new Submission();
        submission.setType(request.getType());
        submission.setStatus(SubmissionStatus.PENDING);
        submission.setMessage(request.getMessage());
        submission.setSender(sender);
        submission.setReceiver(receiver);
        submission.setSenderSchedule(sSched);
        submission.setReceiverSchedule(rSched);
        submission.setExpiredAt(getEarliestStartTime(sSched, rSched).minusMinutes(15));

        if (isDuplicateSubmission(submission, sSched, rSched)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permintaan yang sama sudah pernah dibuat");
        }

        return SubmissionResponse.toSubmissionResponse(submissionRepository.save(submission));
    }

    private Employee fetchEmployee(String id) {
        return employeeRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Karyawan tidak ditemukan"));
    }

    private Schedule fetchSchedule(Employee emp, LocalDate date, boolean isOff) {
        return scheduleRepository.findAllByFillerAndDate(emp, date).stream()
                .filter(s -> isOff == s.getShift().getLabel().equalsIgnoreCase("L"))
                .findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jadwal " + emp.getNickname() + " tidak ditemukan"));
    }

    private boolean isDuplicateSubmission(Submission sub, Schedule sSched, Schedule rSched) {
        Specification<Submission> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("type"), sub.getType()));
            predicates.add(root.get("status").in(SubmissionStatus.PENDING, SubmissionStatus.APPROVED));

            // Pengecekan Keterlibatan Karyawan (A dengan B)
            Predicate sameInvolved = cb.or(
                    cb.and(cb.equal(root.get("sender"), sub.getSender()), cb.equal(root.get("receiver"), sub.getReceiver())),
                    cb.and(cb.equal(root.get("sender"), sub.getReceiver()), cb.equal(root.get("receiver"), sub.getSender()))
            );
            predicates.add(sameInvolved);

            // Pengecekan Jadwal yang Spesifik
            List<Predicate> schedulePredicates = new ArrayList<>();

            if (Objects.nonNull(rSched)) {
                // Untuk SHIFT_SWAP atau OFF_SWAP: Cek apakah pasangan jadwal ini sudah pernah diajukan
                Predicate pair1 = cb.and(cb.equal(root.get("senderSchedule"), sSched), cb.equal(root.get("receiverSchedule"), rSched));
                Predicate pair2 = cb.and(cb.equal(root.get("senderSchedule"), rSched), cb.equal(root.get("receiverSchedule"), sSched));
                schedulePredicates.add(cb.or(pair1, pair2));
            } else {
                // Untuk BACKUP: Cek apakah jadwal pengirim sudah ada yang nge-backup
                schedulePredicates.add(cb.equal(root.get("senderSchedule"), sSched));
            }

            predicates.add(cb.or(schedulePredicates.toArray(new Predicate[0])));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return submissionRepository.exists(spec);
    }

    private void validateCancellationChain(Submission ref) {
        if (!ref.getSenderSchedule().getFiller().equals(ref.getReceiver())
        ) {
            if (Objects.nonNull(ref.getReceiverSchedule())) {
                if (!ref.getReceiverSchedule().getFiller().equals(ref.getSender())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rantai pertukaran sudah berubah, tidak bisa dibatalkan secara otomatis (pertukaran berantai harus di batalkan terlebih dahulu");
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rantai pertukaran sudah berubah, tidak bisa dibatalkan secara otomatis (pertukaran berantai harus di batalkan terlebih dahulu");
        }
    }

    private LocalDateTime getEarliestStartTime(Schedule s, Schedule r) {
        LocalDateTime sStart = convertToSafeDateTime(s);
        if (Objects.isNull(r)) return sStart;

        LocalDateTime rStart = convertToSafeDateTime(r);
        return sStart.isBefore(rStart) ? sStart : rStart;
    }

    private LocalDateTime convertToSafeDateTime(Schedule sched) {
        java.time.LocalTime startTime = sched.getShift().getStart();
        if (startTime == null) {
            startTime = LocalTime.of(5, 50);
        }
        return LocalDateTime.of(sched.getDate(), startTime);
    }

    @Scheduled(cron = "0 15,45 * * * *", zone = "Asia/Makassar")
    @Transactional
    public void updateExpiredSubmissions() {
        List<Submission> expiredList = submissionRepository.findAllByStatus(SubmissionStatus.PENDING).stream()
                .filter(sub -> LocalDateTime.now(ZoneId.of("Asia/Makassar")).isAfter(ChronoLocalDateTime.from(sub.getExpiredAt().atZone(ZoneId.of("Asia/Makassar")))))
                .peek(sub -> sub.setStatus(SubmissionStatus.EXPIRED))
                .toList();

        if (!expiredList.isEmpty()) submissionRepository.saveAll(expiredList);
    }

    @Scheduled(cron = "0 0 18 * * *", zone = "Asia/Makassar")
    @Transactional
    public void automateBackupSubmissions() {
        // 1. Cari semua employee dengan nickname "Kosong"
        List<Employee> emptyEmployees = employeeRepository.findByNickname("Kosong");
        LocalDate tomorrow = LocalDate.now(ZoneId.of("Asia/Makassar")).plusDays(1);

        if (!emptyEmployees.isEmpty()) {
            emptyEmployees.forEach(emptyEmployee -> {
                List<Schedule> emptySchedules = scheduleRepository.findAllByFillerAndDate(emptyEmployee, tomorrow).stream()
                        .filter(s -> !s.getShift().getLabel().equalsIgnoreCase("L")).toList();

                if (!emptySchedules.isEmpty()) {
                    emptySchedules.forEach(emptySchedule -> {
                        List<Employee> candidates = new ArrayList<>();

                        employeeRepository.findByDivisionAndPositionAndStatus(emptyEmployee.getDivision(), emptyEmployee.getPosition(), EmployeeStatus.ACTIVE).forEach(candidate -> {
                            if (ceckCandidate(candidate, tomorrow, emptySchedule) && !candidate.getNickname().equals("Kosong")) {
                                candidates.add(candidate);
                            }
                        });

                        List<Submission> submissions = new ArrayList<>();
                        candidates.forEach(candidate -> {
                            Submission submission = new Submission();
                            submission.setType(SubmissionType.BACKUP);
                            submission.setStatus(SubmissionStatus.PENDING);
                            submission.setMessage("Tolong isi schedule kosong untuk shift " + emptySchedule.getShift().getName() + " besok.");

                            submission.setSender(emptyEmployee);
                            submission.setReceiver(candidate);
                            submission.setSenderSchedule(emptySchedule);

                            LocalDateTime startTime = emptySchedule.getDate().atTime(emptySchedule.getShift().getStart());
                            submission.setExpiredAt(startTime.minusMinutes(15));
                            System.out.println(submission.getReceiver().getNickname());
                            submissions.add(submission);
                        });
                        System.out.println(submissionRepository.saveAll(submissions).size() + " Permintaan berhasil di simpan");
                    });
                }
            });

        }
    }

    private boolean ceckCandidate(Employee candidate, LocalDate date, Schedule emptySchedule) {
        List<Schedule> schedules = scheduleRepository.findAllByFillerAndDate(candidate, date);
        List<Schedule> workSchedules = schedules.stream().filter(schedule -> !schedule.getShift().getLabel().equals("L")).toList();

        if (workSchedules.size() > 1) {
            return false;
        }

        if (workSchedules.stream().findAny().isPresent()) {
            Schedule candidateSchedule = workSchedules.stream().findAny().get();
            if (candidateSchedule.getShift().equals(emptySchedule.getShift())) {
                return false;
            }
            if (candidateSchedule.getShift().getStart().isAfter(emptySchedule.getShift().getStart())) {
                return !(Duration.between(emptySchedule.getShift().getEnd(), candidateSchedule.getShift().getStart()).toHours() < -2);
            } else {
                return !(Duration.between(candidateSchedule.getShift().getEnd(), emptySchedule.getShift().getStart()).toHours() < -2);
            }

        }
        return true;
    }
}