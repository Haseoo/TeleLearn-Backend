package kielce.tu.weaii.telelearn.repositories.jpa;

import kielce.tu.weaii.telelearn.models.courses.Path;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PathJPARepository extends JpaRepository<Path, Long> {
}
