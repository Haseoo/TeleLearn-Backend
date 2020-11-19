package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.repositories.jpa.DayPlanJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.DayPlanRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DayPlanRepositoryImpl extends BaseCRUDRepositoryImpl<TaskScheduleRecord> implements DayPlanRepository {
    private final DayPlanJPARepository jpaRepository;

    public DayPlanRepositoryImpl(DayPlanJPARepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }
}
