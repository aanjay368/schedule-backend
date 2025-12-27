package id.my.schedule.service;

import id.my.schedule.entity.Color;
import id.my.schedule.model.shift.CreateShiftRequest;
import id.my.schedule.model.shift.UpdateShiftRequest;
import id.my.schedule.model.shift.ShiftResponse;

import java.util.List;

public interface ShiftService {

    List<Color> getColors();

    ShiftResponse add(CreateShiftRequest request);

    List<ShiftResponse> getList(Integer divisionId, Integer positionId);

    ShiftResponse update(UpdateShiftRequest request);

    ShiftResponse delete(Integer shiftId);

}
