package kielce.tu.weaii.telelearn.repositories.jpa;

import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayPlanJPARepository extends JpaRepository<TaskScheduleRecord, Long> {
}
