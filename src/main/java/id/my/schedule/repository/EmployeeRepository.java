package id.my.schedule.repository;

import id.my.schedule.entity.Employee;
import id.my.schedule.entity.Division;
import id.my.schedule.entity.Position;
import id.my.schedule.entity.enum_entity.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String>, JpaSpecificationExecutor<Employee> {

    List<Employee> findByNickname(String nickname);

    List<Employee> findByDivisionAndPositionAndStatus(Division division, Position position, EmployeeStatus status);

    List<Employee> findByDivisionAndPositionAndNickname( Division division, Position position,String nickname);

}
