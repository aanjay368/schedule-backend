package id.my.schedule.service.implementation;

import id.my.schedule.entity.*;
import id.my.schedule.entity.enum_entity.EmployeeStatus;
import id.my.schedule.model.employee.CreateEmployeeRequest;
import id.my.schedule.model.employee.SearchEmployeeRequest;
import id.my.schedule.model.employee.UpdateEmployeeRequest;
import id.my.schedule.model.employee.EmployeeResponse;
import id.my.schedule.repository.*;
import id.my.schedule.service.EmployeeService;
import jakarta.persistence.criteria.Predicate;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    @Override
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {

        validatorService.validate(request);

        Division division = divisionRepository.findById(request.getDivisionId()).orElse(null);
        Position position = positionRepository.findById(request.getPositionId()).orElse(null);

        User user = new User();
        user.setUsername(request.getNickname().toLowerCase().replace(" ", "_"));
        user.setPassword(BCrypt.hashpw("liongroup1", BCrypt.gensalt()));
        User savedUser = userRepository.save(user);

        Employee employee = new Employee();
        employee.setNickname(request.getNickname());
        employee.setFullname(request.getFullname());
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDivision(division);
        employee.setUser(savedUser);
        employee.setPosition(position);
        Employee savedEmpoyee = employeeRepository.save(employee);

        return EmployeeResponse.toEmployeeResponse(savedEmpoyee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchEmployee(SearchEmployeeRequest request) {

        Specification<Employee> specification = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(request.getName())) {
                predicates.add(cb.and(
                        cb.or(
                                cb.like(root.get("nickname"), "%" + request.getName() + "%"),
                                cb.like(root.get("fullname"), "%" + request.getName() + "%")
                        )
                ));
            }
            if (Objects.nonNull(request.getDivisionId())) {
                predicates.add(cb.and(cb.equal(root.get("division").get("id"), request.getDivisionId())));
            }

            if (Objects.nonNull(request.getPosisitionId())) {
                predicates.add(cb.and(cb.equal(root.get("position").get("id"), request.getPosisitionId())));
            }


            return cq.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        List<Employee> employees = employeeRepository.findAll(specification);

        if (employees.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Karyawan belum ada");
        }

        return employees.stream()
                .filter(e -> !e.getStatus().equals(EmployeeStatus.RESIGNED) && !e.getStatus().equals(EmployeeStatus.DISMISSED) && !e.getNickname().equalsIgnoreCase("Kosong"))
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

        if (employee.getStatus().equals(EmployeeStatus.RESIGNED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Karyawan sudah Resign");
        }

        if (employee.getStatus().equals(EmployeeStatus.DISMISSED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Karyawan sudah Dikeluarkan");
        }

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

        if (employee.getStatus().equals(EmployeeStatus.RESIGNED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data karyawan sudah Resign");
        }

        if (employee.getStatus().equals(EmployeeStatus.DISMISSED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data karyawan sudah Dikeluarkan");
        }

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
            schedule.setOwner(null);
            scheduleRepository.save(schedule);
        });

        employeeRepository.delete(employee);
        userRepository.delete(employee.getUser());


        return  EmployeeResponse.toEmployeeResponse(employee);
    }


}
