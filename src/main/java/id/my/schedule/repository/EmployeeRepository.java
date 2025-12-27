package id.my.schedule.repository;

import id.my.schedule.entity.Employee;
import id.my.schedule.entity.Division;
import id.my.schedule.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    List<Employee> findByDivisionAndPosition( Division division, Position position);

    boolean existsByNickname(String nickname);



}
