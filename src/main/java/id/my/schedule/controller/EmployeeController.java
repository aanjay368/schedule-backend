package id.my.schedule.controller;

import id.my.schedule.entity.User;
import id.my.schedule.model.employee.CreateEmployeeRequest;
import id.my.schedule.model.employee.UpdateEmployeeRequest;
import id.my.schedule.model.employee.EmployeeResponse;
import id.my.schedule.model.WebResponse;
import id.my.schedule.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping(
            path = "/api/v1/employees",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> create(@RequestBody CreateEmployeeRequest request){
        EmployeeResponse response = employeeService.create(request);
        return WebResponse. <EmployeeResponse>builder().status(HttpStatus.OK.value()).data(response).build();
    }

    @GetMapping(
            path = "/api/v1/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<EmployeeResponse>> geEmployeeList(){
        List<EmployeeResponse> response = employeeService.getList();
        return WebResponse.<List<EmployeeResponse>>builder().status(200).data(response).build();
    }

    @GetMapping(
            path = "/api/v1/employees/{employeeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> getById(@PathVariable(name = "employeeId") String employeeId){
        EmployeeResponse response = employeeService.get(employeeId);
        return WebResponse.<EmployeeResponse>builder().status(HttpStatus.OK.value()).data(response).build();
    }

    @GetMapping(
            path = "/api/v1/employees/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> getCurrentEmployee(User user){
        EmployeeResponse response = employeeService.getCurrent(user);
        return WebResponse.<EmployeeResponse>builder().data(response).build();
    }

    @PutMapping(
            path = "/api/v1/employees/{employeeId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> update(@PathVariable(name = "employeeId") String employeeId, @RequestBody UpdateEmployeeRequest request){
        request.setEmployeeId(employeeId);
        EmployeeResponse response = employeeService.update(request);
        return WebResponse.<EmployeeResponse>builder().status(HttpStatus.OK.value()).data(response).build();
    }

    @DeleteMapping(
            path = "/api/v1/employees/{employeeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> delete(@PathVariable(name = "employeeId") String employeeId){
        EmployeeResponse response = employeeService.delete(employeeId);
        return WebResponse.<EmployeeResponse>builder().status(HttpStatus.OK.value()).data(response).build();
    }

}
