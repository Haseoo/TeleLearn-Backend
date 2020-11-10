package kielce.tu.weaii.telelearn.repositories.jpa;

import kielce.tu.weaii.telelearn.models.courses.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskJPARepository extends JpaRepository<Task, Long> {
}
