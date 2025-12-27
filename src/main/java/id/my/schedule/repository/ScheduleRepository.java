package id.my.schedule.repository;

import id.my.schedule.entity.Division;
import id.my.schedule.entity.Position;
import id.my.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String>, JpaSpecificationExecutor<Schedule> {

    List<Schedule> findByDateAndDivision(LocalDate date, Division division);

    void deleteByDivisionAndPositionAndDateBetween(Division division, Position position, LocalDate startData, LocalDate endDate);
}
