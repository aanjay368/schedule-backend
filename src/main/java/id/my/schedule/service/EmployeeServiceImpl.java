package id.my.schedule.service;

import id.my.schedule.entity.Employee;
import id.my.schedule.entity.EmployeeDivision;
import id.my.schedule.entity.EmployeePosition;
import id.my.schedule.entity.UserRole;
import id.my.schedule.model.CreateEmployeeRequest;
import id.my.schedule.model.EditEmployeeRequest;
import id.my.schedule.model.EmployeeResponse;
import id.my.schedule.repository.EmployeeRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ValidatorService validatorService;

    private final ConcurrentHashMap<String, Employee> employeesProvider = new ConcurrentHashMap<>();

    @Override
    @Async
    @Transactional
    public Future<EmployeeResponse> add(CreateEmployeeRequest request) {
        validatorService.validate(request);

        Employee employee = new Employee();
        employee.setId(UUID.randomUUID().toString().replace("-", ""));
        employee.setNickname(request.getNickname());
        employee.setFullname(request.getFullname().toUpperCase());
        employee.setRole(UserRole.EMPLOYEE);
        employee.setDivision(EmployeeDivision.valueOf(request.getDivision()));
        employee.setPosition(EmployeePosition.WORKER);
        employee.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));

        employeeRepository.save(employee);
        return CompletableFuture.completedFuture(toEmployeeResponse(employee));
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    public Future<List<EmployeeResponse>> getEmployeeList() {
        List<Employee> employees = employeeRepository.findAll();
        return CompletableFuture.completedFuture(employees.stream().map((EmployeeServiceImpl::toEmployeeResponse)).toList());
    }

    @Override
    @Async
    @Transactional
    public Future<EmployeeResponse> edit(EditEmployeeRequest request) {
        validatorService.validate(request);

        Employee employee = employeeRepository.findById(request.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Karyawan dengan ID " + request.getId() + " tidak di temukan")
        );

        if (Objects.nonNull(request.getNickname())){
            if (employeeRepository.existsByNickname(request.getNickname()) > 0){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nama panggilan sudah ada");
            }
            if (request.getNickname().length() < 3 && request.getNickname().length() > 20){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "panjang nama panggilan minimal 3 sampai 20 karakter");
            }

            employee.setNickname(request.getNickname());

            if (request.getNickname().toLowerCase().contains("kosong")){
                employee.setPassword(BCrypt.hashpw("slebew", BCrypt.gensalt()));
            }
        }

        if (Objects.nonNull(request.getFullname())){
            if (request.getFullname().length() < 3 || request.getFullname().length() > 100){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "panjang nama lengkap minimal 3 sampai 20 karakter");
            }
            employee.setFullname(request.getFullname().toUpperCase());
        }

        if (Objects.nonNull(request.getPosition())){
            employee.setPosition(EmployeePosition.safeValueOf(request.getPosition()));
        }

        if (Objects.nonNull(request.getDivision())){
            employee.setDivision(EmployeeDivision.safeValueOf(request.getDivision()));
        }

        employeeRepository.save(employee);

        return CompletableFuture.completedFuture(toEmployeeResponse(employee));
    }

    @Override
    @Async
    @Transactional
    public Future<EmployeeResponse> delete(String id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee with nickname " + id + " Not Found")
        );

        employeeRepository.delete(employee);

        return CompletableFuture.completedFuture(toEmployeeResponse(employee));
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    public Future<EmployeeResponse> getById(String id){
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data karyawan tidak di temukan")
        );

        return CompletableFuture.completedFuture(toEmployeeResponse(employee));
    }

    public static EmployeeResponse toEmployeeResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getNickname(),
                employee.getRole(),
                employee.getFullname(),
                employee.getDivision(),
                employee.getPosition(),
                employee.getNumber()
        );
    }

}
