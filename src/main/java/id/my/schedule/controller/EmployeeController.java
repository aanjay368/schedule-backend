package id.my.schedule.controller;

import id.my.schedule.model.CreateEmployeeRequest;
import id.my.schedule.model.EditEmployeeRequest;
import id.my.schedule.model.EmployeeResponse;
import id.my.schedule.model.WebResponse;
import id.my.schedule.service.EmployeeService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @SneakyThrows
    @PostMapping(
            path = "/api/v1/employees",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> add(@RequestBody CreateEmployeeRequest request){
        EmployeeResponse response = employeeService.add(request).get();
        return WebResponse.<EmployeeResponse>builder().status(HttpStatus.OK.value()).data(response).build();
    }

    @SneakyThrows
    @GetMapping(
            path = "/api/v1/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<EmployeeResponse>> geEmployeeList(){
        List<EmployeeResponse> response = employeeService.getEmployeeList().get();
        return WebResponse.<List<EmployeeResponse>>builder().status(200).data(response).build();
    }

    @SneakyThrows
    @GetMapping(
            path = "/api/v1/employees/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> getById(@PathVariable(name = "id") String id){
        EmployeeResponse response = employeeService.getById(id).get();
        return WebResponse.<EmployeeResponse>builder().status(HttpStatus.OK.value()).data(response).build();
    }

    @SneakyThrows
    @PatchMapping(
            path = "/api/v1/employees/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> edit(@PathVariable(name = "id") String id, @RequestBody EditEmployeeRequest request){
        request.setId(id);
        EmployeeResponse response = employeeService.edit(request).get();
        return WebResponse.<EmployeeResponse>builder().status(HttpStatus.OK.value()).data(response).build();
    }

    @SneakyThrows
    @DeleteMapping(
            path = "/api/v1/employees/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> delete(@PathVariable(name = "id") String id){
        EmployeeResponse response = employeeService.delete(id).get();
        return WebResponse.<EmployeeResponse>builder().status(HttpStatus.OK.value()).data(response).build();
    }

}
