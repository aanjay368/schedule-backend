package id.my.schedule.validator.implementation;

import id.my.schedule.entity.Employee;
import id.my.schedule.entity.Schedule;
import id.my.schedule.model.submission.CreateSubmissionRequest;
import id.my.schedule.repository.EmployeeRepository;
import id.my.schedule.repository.ScheduleRepository;
import id.my.schedule.validator.annotation.ValidSubmission;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class SubmissionValidator implements ConstraintValidator<ValidSubmission, CreateSubmissionRequest> {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.of("ID", "id"));

    @Override
    public boolean isValid(CreateSubmissionRequest request, ConstraintValidatorContext context) {
        Employee sender = request.getSender();

        if (Objects.isNull(request.getType())) {
            return addError(context, "Tipe Permintaan tidak boleh kosong", "type");
        }

        if (Objects.isNull(request.getReceiverId()) || request.getReceiverId().isBlank()) {
            return addError(context, "Penerima harus diisi", "receiverId");
        }

        return switch (request.getType()) {
            case SHIFT_SWAP -> validateShiftSwap(sender, request, context);
            case OFF_SWAP -> validateOffSwap(sender, request, context);
            case BACKUP -> validateBackup(sender, request, context);
            default -> true;
        };
    }

    private boolean validateShiftSwap(Employee sender, CreateSubmissionRequest request, ConstraintValidatorContext context) {

        if (Objects.isNull(request.getDate())) {
            return addError(context, "Tanggal harus diisi", "date");
        }

        if (isPastDate(request.getDate())) {
            return addError(context, "tanggal sudah lewat", "date");
        }

        Employee receiver = employeeRepository.findById(request.getReceiverId()).orElse(null);
        Schedule sSched = getWorkDay(sender, request.getDate());
        Schedule rSched =  getWorkDay(receiver, request.getDate());

        if (Objects.isNull(receiver)) {
            return addError(context, "Data penerima tidak di temukan ", "receiverId");
        }

        // validasi di fullin
        if (Objects.isNull(sSched) && !isOffDay(sender, request.getDate()) && scheduleRepository.existsByOwnerAndDate(sender, request.getDate())) {
            return addError(context, "Jadwal kamu di fullin pada tanggal " + request.getDate().format(formatter), "date");
        }

        if (Objects.isNull(rSched) && !isOffDay(receiver, request.getDate()) && scheduleRepository.existsByOwnerAndDate(sender, request.getDate())) {
            return addError(context,  receiver.getNickname() + " di fullin pada tanggal " + request.getDate().format(formatter), "date");
        }

        //validasi libur
        if (isWorkDay(sender, request.getDate()) && isOffDay(sender, request.getDate())) {
            return addError(context, "Kamu libur di tanggal " + request.getDate().format(formatter), "date");
        }

        if (isWorkDay(receiver, request.getDate()) && isOffDay(receiver, request.getDate())) {
            return addError(context, receiver.getNickname()  + " libur di tanggal " + request.getDate().format(formatter) + " (Ganti rekan yang lain)", "receiverId");
        }

        //validasi ngefull
        if (isBusy(sender, request.getDate())) {
            return addError(context, "Kamu ngefull di tanggal " + request.getDate().format(formatter), "date");
        }

        if (isBusy(receiver, request.getDate())) {
            return addError(context, receiver.getNickname() + " ngefull di tanggal " + request.getDate().format(formatter) + " (Ganti rekan yang lain)", "receiverId");
        }


        //validasi schedule
        if (Objects.isNull(sSched) || Objects.isNull(rSched)) {
            return addError(context, "Jadwal kamu atau rekan tidak ditemukan pada tanggal tersebut", "date");
        }

        //validasi shift
        if (sSched.getShift().equals(rSched.getShift())) {
            return addError(context, "Tidak bisa menukar ke shift yang sama (Ganti rekan yang lain)", "receiverId");
        }

        return true;
    }

    private boolean validateOffSwap(Employee sender, CreateSubmissionRequest request, ConstraintValidatorContext context) {

        if (Objects.isNull(request.getSenderDate())) {
            return addError(context, "Tanggal libur kamu harus diisi", "senderDate");
        }

        if (Objects.isNull(request.getReceiverDate())) {
            return addError(context, "Tanggal libur rekan harus diisi", "receiverDate");
        }

        if (isPastDate(request.getSenderDate())) {
            return addError(context, "tanggal sudah lewat", "senderDate");
        }

        if (isPastDate(request.getReceiverDate())) {
            return addError(context, "tanggal sudah lewat", "receiverDate");
        }

        Employee receiver = employeeRepository.findById(request.getReceiverId()).orElse(null);

        if (Objects.isNull(receiver)) {
            return addError(context, "Karyawan penerima tidak ditemukan", "receiverId");
        }

        //validasi libur pada tanggal libur sendiri
        if (!isOffDay(sender, request.getSenderDate())) {
            return addError(context, "Kamu tidak libur di tanggal " + request.getSenderDate().format(formatter), "senderDate");
        }

        if (!isOffDay(receiver, request.getReceiverDate())) {
            return addError(context, receiver.getNickname()  + " tidak libur di tanggal " + request.getReceiverDate().format(formatter) + " (Ganti rekan yang lain atau ganti tanggal)", "receiverId", "receiverDate");
        }

        //validasi libur pada tanggal libur yang di targetkan
        if (isOffDay(sender, request.getReceiverDate())) {
            return addError(context, "Kamu sudah libur di tanggal " + request.getSenderDate().format(formatter) + " Ganti tanggal libur kamu)", "senderDate");
        }

        if (isOffDay(receiver, request.getSenderDate())) {
            return addError(context, receiver.getNickname() + " libur juga di tanggal " + request.getReceiverDate().format(formatter) + " (Ganti tanggal libur rekan atau ganti rekan)", "receiverDate", "receiverId");
        }

        //validasi ngefull pada tanggal libur sendiri
        if (isBusy(sender, request.getSenderDate())) {
            return addError(context, "Kamu ngefull 2 orang di tanggal " + request.getSenderDate().format(formatter), "senderDate");
        }

        if (isBusy(receiver, request.getReceiverDate())) {
            return addError(context, receiver.getNickname() + " ngefull 2 orang di tanggal " + request.getReceiverDate().format(formatter) + " (Ganti rekan yang lain atau ganti tanggal)",  "receiverId","receiverDate");
        }

        // validasi di fullin pada tanggal yang di targetkan
        if (isWorkDay(sender, request.getReceiverDate()) && scheduleRepository.existsByOwnerAndDate(sender, request.getReceiverDate())) {
            return addError(context, "Jadwal kamu di fullin pada tanggal " + request.getReceiverDate().format(formatter), "receiverDate");
        }

        if (isWorkDay(receiver, request.getSenderDate()) && scheduleRepository.existsByOwnerAndDate(sender, request.getSenderDate())) {
            return addError(context,  receiver.getNickname() + " di fullin pada tanggal " + request.getSenderDate().format(formatter), "senderDate", "receiverId");
        }


        if (request.getSenderDate().equals(request.getReceiverDate())) {
            return addError(context, "Kamu tidak bisa tukar libur di tanggal yang sama (Ganti tanggal libur kamu atau ganti tanggal libur rekan)", "senderDate", "receiverDate");
        }

        return true;
    }


    private boolean validateBackup(Employee sender, CreateSubmissionRequest request, ConstraintValidatorContext context) {

        if (Objects.isNull(request.getDate())) {
            return addError(context, "Tanggal harus diisi", "date");
        }

        if (isPastDate(request.getDate())) {
            return addError(context, "tanggal sudah lewat", "date");
        }

        Employee receiver = employeeRepository.findById(request.getReceiverId()).orElse(null);
        Schedule sSched = getWorkDay(sender, request.getDate());
        Schedule rSched =  getWorkDay(receiver, request.getDate());

        if (Objects.isNull(receiver)) {
            return addError(context, "Data penerima tidak di temukan ", "receiverId");
        }

        // validasi di fullin
        if (Objects.isNull(sSched) && !isOffDay(sender, request.getDate()) && scheduleRepository.existsByOwnerAndDate(sender, request.getDate())) {
            return addError(context, "Jadwal kamu di fullin pada tanggal " + request.getDate().format(formatter), "date");
        }

        //validasi libur
        if (isWorkDay(sender, request.getDate()) || isOffDay(sender, request.getDate())) {
            return addError(context, "Kamu libur di tanggal " + request.getDate().format(formatter), "date");
        }

        //validasi ngefull
        if (isBusy(receiver, request.getDate())) {
            return addError(context, receiver.getNickname() + " ngefull di tanggal " + request.getDate().format(formatter) + " (Ganti rekan yang lain)", "receiverId", "date");
        }

        //validasi schedule
        if (Objects.isNull(sSched)) {
            return addError(context, "Jadwal kamu atau rekan tidak ditemukan pada tanggal tersebut", "date");
        }

        //validasi shift
        if (Objects.nonNull(rSched)) {
            if (sSched.getShift().equals(rSched.getShift())) {
                return addError(context, "Tidak bisa minta fullin ke shift yang sama (Ganti rekan yang lain)", "receiverId");
            }
        }

        return true;
    }

    private Schedule getWorkDay(Employee employee, LocalDate date) {
        return scheduleRepository.findAllByFillerAndDate(employee, date)
                .stream()
                .filter(s -> !s.getShift().getLabel().equalsIgnoreCase("L"))
                .findFirst()
                .orElse(null);
    }

    private boolean isBusy(Employee employee, LocalDate date) {

        Stream<Schedule> schedules = scheduleRepository.findAllByFillerAndDate(employee, date)
                .stream().filter(schedule -> !schedule.getShift().getLabel().equalsIgnoreCase("L"));

        return schedules.count() >= 2;
    }

    private boolean isWorkDay(Employee employee, LocalDate date) {

        Stream<Schedule> schedules = scheduleRepository.findAllByFillerAndDate(employee, date)
                .stream().filter(schedule -> !schedule.getShift().getLabel().equalsIgnoreCase("L"));

        return schedules.findAny().isEmpty();
    }

    private boolean isOffDay(Employee employee, LocalDate date) {

        Stream<Schedule> schedules = scheduleRepository.findAllByFillerAndDate(employee, date)
                .stream().filter(schedule -> schedule.getShift().getLabel().equalsIgnoreCase("L"));

        return schedules.findAny().isPresent();
    }

    private boolean isPastDate(LocalDate date) {

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Makassar"));

        return now.isAfter(date.atTime(5, 0));
    }

    private boolean addError(ConstraintValidatorContext context, String message, String... nodes) {
        context.disableDefaultConstraintViolation();
        for (String node : nodes) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(node)
                    .addConstraintViolation();
        }
        return false;
    }
}