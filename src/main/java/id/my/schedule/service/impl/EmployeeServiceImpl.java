package id.my.schedule.service.impl;

import id.my.schedule.entity.*;
import id.my.schedule.model.employee.CreateEmployeeRequest;
import id.my.schedule.model.employee.UpdateEmployeeRequest;
import id.my.schedule.model.employee.EmployeeResponse;
import id.my.schedule.repository.*;
import id.my.schedule.service.EmployeeService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ValidatorService validatorService;

    private final ConcurrentHashMap<String, Employee> employeesProvider = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {

        validatorService.validate(request);

        Division division = divisionRepository.findById(request.getDivisionId()).orElse(null);
        Position position = positionRepository.findById(request.getPositionId()).orElse(null);

        User user = new User();
        user.setUsername(request.getNickname().toLowerCase().replace(" ", "_"));
        user.setPassword(BCrypt.hashpw("usercgp1", BCrypt.gensalt()));
        user.setTheme(UserTheme.AUTO);
        User savedUser = userRepository.save(user);

        Employee employee = new Employee();
        employee.setNickname(request.getNickname());
        employee.setFullname(request.getFullname());
        employee.setDivision(division);
        employee.setUser(savedUser);
        employee.setPosition(position);
        Employee savedEmpoyee = employeeRepository.save(employee);

        return EmployeeResponse.toEmployeeResponse(savedEmpoyee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getList() {
        List<Employee> employees = employeeRepository.findAll();

        if (employees.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Karyawan belum ada");
        }

        return employees.stream()
                .sorted(Comparator.comparing(Employee::getNickname))
                .map((EmployeeResponse::toEmployeeResponse))
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse get(String employeId){

        Employee employee = employeeRepository.findById(employeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data karyawan tidak di temukan")
        );

        return EmployeeResponse.toEmployeeResponse(employee);
    }

    @Override
    public EmployeeResponse getCurrent(User user) {
        return EmployeeResponse.toEmployeeResponse(user.getEmployee());
    }

    @Override
    @Transactional
    public EmployeeResponse update(UpdateEmployeeRequest request) {

        validatorService.validate(request);

        Division division = divisionRepository.findById(request.getDivisionId()).orElse(null);
        Position position = positionRepository.findById(request.getPositionId()).orElse(null);

        Employee employee = employeeRepository.findById(request.getEmployeeId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Karyawan tidak di temukan")
        );
        employee.setNickname(request.getNickname());
        employee.setFullname(request.getFullname());
        employee.setDivision(division);
        employee.setPosition(position);
        Employee savedEmployee = employeeRepository.save(employee);

        return  EmployeeResponse.toEmployeeResponse(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeResponse delete(String employeeIdd) {

        Employee employee = employeeRepository.findById(employeeIdd).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Karyawan tidak di temukan")
        );

        employee.getSchedules().forEach(schedule -> {
            schedule.setEmployee(null);
            scheduleRepository.save(schedule);
        });

        employeeRepository.delete(employee);
        userRepository.delete(employee.getUser());


        return  EmployeeResponse.toEmployeeResponse(employee);
    }


}
