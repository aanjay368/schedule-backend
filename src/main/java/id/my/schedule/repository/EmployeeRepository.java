package id.my.schedule.repository;

import id.my.schedule.entity.Employee;
import id.my.schedule.entity.EmployeeDivision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findFirstByFullnameAndDivision(String fullname, EmployeeDivision division);

    @Query(value = "SELECT COUNT(*) > 0 FROM employees e " +
            "JOIN users u ON e.id = u.id " +
            "WHERE BINARY u.nickname = :nickname", nativeQuery = true)
    Long existsByNickname(String nickname);

    Boolean existsByFullname(String fullname);

}
