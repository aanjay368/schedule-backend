package id.my.schedule.service.implementation;

import id.my.schedule.entity.Division;
import id.my.schedule.entity.Position;
import id.my.schedule.entity.Shift;
import id.my.schedule.model.shift.CreateShiftRequest;
import id.my.schedule.model.shift.UpdateShiftRequest;
import id.my.schedule.model.shift.ShiftResponse;
import id.my.schedule.repository.DivisionRepository;
import id.my.schedule.repository.PositionRepository;
import id.my.schedule.repository.ShiftRepository;
import id.my.schedule.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class ShiftServiceImpl implements ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Override
    @Transactional
    public ShiftResponse add(CreateShiftRequest request) {

        validatorService.validate(request);

        Division division = divisionRepository.findById(request.getDivisionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data Divisi tidak di temukan")
        );

        Position position = positionRepository.findById(request.getPositionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data Posisi tidak di temukan")
        );

        Shift shift = new Shift();
        shift.setName(request.getName());
        shift.setLabel(request.getLabel());
        shift.setStart(request.getStart());
        shift.setEnd(request.getEnd());
        shift.setColor(request.getColor());
        shift.setDivision(division);
        shift.setPosition(position);

        shiftRepository.save(shift);
        return ShiftResponse.toShiftResponse(shift);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftResponse> getList(Integer divisionId, Integer positionId) {

        Division division = divisionRepository.findById(divisionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Divisi tidak ditemukan")
        );

        Position position = positionRepository.findById(positionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Divisi tidak ditemukan")
        );

        List<Shift> shifts = shiftRepository.findByDivisionAndPosition(division, position);

        if (shifts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Shift tidak di temukan");
        }

        return shifts.stream()
                .sorted(Comparator.comparing(
                        Shift::getStart,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(ShiftResponse::toShiftResponse)
                .toList();
    }

    @Override
    @Transactional
    public ShiftResponse update(UpdateShiftRequest request) {

        validatorService.validate(request);

        Shift shift = shiftRepository.findById(request.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Shift tidak ditemukan")
        );

        shift.setName(request.getName());
        shift.setLabel(request.getLabel());
        shift.setStart(request.getStart());
        shift.setEnd(request.getEnd());
        shift.setColor(request.getColor());

        shiftRepository.save(shift);

        return ShiftResponse.toShiftResponse(shift);

    }

    @Override
    @Transactional
    public ShiftResponse delete(Integer shiftId) {

        Shift shift = shiftRepository.findById(shiftId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Shift tidak di temukan")
        );

        if (!shift.getSchedules().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Data Shift masih terhubung ke beberapa schedule");
        }

        shiftRepository.delete(shift);

        return ShiftResponse.toShiftResponse(shift);
    }

}
