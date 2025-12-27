package id.my.schedule.service;

import id.my.schedule.entity.User;
import id.my.schedule.model.employee.CreateEmployeeRequest;
import id.my.schedule.model.employee.UpdateEmployeeRequest;
import id.my.schedule.model.employee.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse create(CreateEmployeeRequest request);

    List<EmployeeResponse> getList();

    EmployeeResponse get(String employeeId);

    EmployeeResponse getCurrent(User user);

    EmployeeResponse update(UpdateEmployeeRequest request);

    EmployeeResponse delete(String employeeId);

}
