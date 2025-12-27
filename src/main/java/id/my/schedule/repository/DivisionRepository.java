package id.my.schedule.repository;

import id.my.schedule.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Integer> {

    boolean existsByName(String name);

    Optional<Division> findByName(String name);
}
