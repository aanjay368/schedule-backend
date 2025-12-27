package id.my.schedule.controller;

import id.my.schedule.entity.Color;
import id.my.schedule.model.shift.CreateShiftRequest;
import id.my.schedule.model.shift.UpdateShiftRequest;
import id.my.schedule.model.shift.ShiftResponse;
import id.my.schedule.model.WebResponse;
import id.my.schedule.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping(
            path = "/api/v1/shifts/colors",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<Color>> getColors(
    ){

        List<Color> response = shiftService.getColors();

        return WebResponse.<List<Color>>builder().data(response).build();
    }

    @PostMapping(
            path = "/api/v1/shifts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ShiftResponse> addShift(@RequestBody CreateShiftRequest request){

        ShiftResponse response = shiftService.add(request);

        return WebResponse.<ShiftResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/api/v1/shifts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ShiftResponse>> get(
            @RequestParam(name = "division") Integer division,
            @RequestParam(name = "position") Integer position
    ){

        List<ShiftResponse> response = shiftService.getList(division, position);

        return WebResponse.<List<ShiftResponse>>builder().data(response).build();
    }

    @PutMapping(
            path = "/api/v1/shifts/{shiftId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ShiftResponse> update(@PathVariable("shiftId") Integer shiftId, @RequestBody UpdateShiftRequest request){

        request.setId(shiftId);
        ShiftResponse response = shiftService.update(request);

        return WebResponse.<ShiftResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/api/v1/shifts/{shiftId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ShiftResponse> delete(@PathVariable("shiftId") Integer shiftId){

        ShiftResponse response = shiftService.delete(shiftId);

        return WebResponse.<ShiftResponse>builder().data(response).build();
    }
}
