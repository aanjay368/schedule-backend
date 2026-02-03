package id.my.schedule;

import static org.junit.jupiter.api.Assertions.*;

import id.my.schedule.entity.*;
import id.my.schedule.entity.enum_entity.ShiftColor;
import id.my.schedule.repository.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class ScheduleTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    void test() {

        User user = new User();
        user.setUsername("developer");
        user.setPassword(BCrypt.hashpw("Sasageyo1.", BCrypt.gensalt()));
        User savedUser = userRepository.save(user);

        Position position = positionRepository.findById(7).orElse(null);
        Division division = divisionRepository.findById(6).orElse(null);

        Employee employee = new Employee();
        employee.setNickname("Developer");
        employee.setFullname("Developer");
        employee.setUser(savedUser);
        employee.setPosition(position);
        employee.setDivision(division);
        employeeRepository.save(employee);
    }

    @Test
    @SneakyThrows
    void addLibur() {

        List<Division> divisions = divisionRepository.findAll();


        divisions.forEach(division -> {
            division.getPositions().forEach(position -> {
                Shift shift = new Shift();
                shift.setName("Libur");
                shift.setLabel("L");
                shift.setColor(ShiftColor.RED);
                shift.setStart(null);
                shift.setEnd(null);
                shift.setDivision(division);
                shift.setPosition(position);
                shiftRepository.save(shift);
            });
        });

    }

    void sdasd() {
        List<User> users = userRepository.findAll();
        List<User> list = users.stream()
                .filter(user -> !user.getUsername().equalsIgnoreCase("developer"))
                .peek(user -> user.setPassword(BCrypt.hashpw("liongroup1", BCrypt.gensalt()))).toList();

        userRepository.saveAll(list);
    }
}
