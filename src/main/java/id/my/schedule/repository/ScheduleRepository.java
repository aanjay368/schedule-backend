package id.my.schedule.repository;

import id.my.schedule.entity.Division;
import id.my.schedule.entity.Employee;
import id.my.schedule.entity.Position;
import id.my.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String>, JpaSpecificationExecutor<Schedule> {

    List<Schedule> findByDivisionAndPositionAndDateBetween(Division division, Position position, LocalDate startDate, LocalDate endDate);

    boolean existsByOwnerAndDate(Employee employee, LocalDate date);

    List<Schedule> findAllByFillerAndDate(Employee filler, LocalDate date);

}
