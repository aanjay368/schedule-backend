package id.my.schedule.service;

import id.my.schedule.model.CreateEmployeeRequest;
import id.my.schedule.model.EditEmployeeRequest;
import id.my.schedule.model.EmployeeResponse;

import java.util.List;
import java.util.concurrent.Future;

public interface EmployeeService {

    Future<EmployeeResponse> add(CreateEmployeeRequest request);

    Future<List<EmployeeResponse>> getEmployeeList();


    Future<EmployeeResponse> edit(EditEmployeeRequest request);

    Future<EmployeeResponse> delete(String nickname);

    Future<EmployeeResponse> getById(String id);
 }
