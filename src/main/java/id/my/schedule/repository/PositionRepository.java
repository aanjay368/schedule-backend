package id.my.schedule.repository;

import id.my.schedule.entity.Division;
import id.my.schedule.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {

    boolean existsByName(String name);

    Optional<Division> findByName(String name);

    List<Position> findByDivisions(Division division);

}
