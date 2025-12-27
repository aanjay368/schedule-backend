package id.my.schedule.controller;

import id.my.schedule.model.division.DivisionResponse;
import id.my.schedule.model.WebResponse;
import id.my.schedule.service.DivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DivisionController {

    @Autowired
    private DivisionService divisionService;

    @GetMapping(
            path = "/api/v1/divisions",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DivisionResponse>> get(){
        List<DivisionResponse> divisions = divisionService.getList();
        return WebResponse.<List<DivisionResponse>>builder().data(divisions).build();
    }

}
