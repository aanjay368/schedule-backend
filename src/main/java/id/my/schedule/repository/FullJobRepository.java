package id.my.schedule.repository;

import id.my.schedule.entity.FullJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FullJobRepository extends JpaRepository<FullJob, String> {
}
