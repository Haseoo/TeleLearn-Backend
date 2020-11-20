package kielce.tu.weaii.telelearn.repositories.ports;

import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;

public interface TaskScheduleRepository extends BaseCRUDRepository<TaskScheduleRecord> {
    void deleteAllStudentRecord(Long studentId);
}
