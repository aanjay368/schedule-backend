package id.my.schedule.repository;

import id.my.schedule.entity.Division;
import id.my.schedule.entity.Position;
import id.my.schedule.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {

    List<Shift> findByDivisionAndPosition(Division division, Position position);


}
