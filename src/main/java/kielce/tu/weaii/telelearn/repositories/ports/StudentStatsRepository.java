package kielce.tu.weaii.telelearn.repositories.ports;

import kielce.tu.weaii.telelearn.models.StudentStatsRecord;

import java.util.List;
import java.util.Optional;

public interface StudentStatsRepository {
    List<StudentStatsRecord> getStudentStat(Long studentId);

    StudentStatsRecord save(StudentStatsRecord record);

    Optional<StudentStatsRecord> getByScheduleId(Long id);

    void delete(StudentStatsRecord record);
}
