package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.servicedata.StudentStats;

import java.time.LocalDate;
import java.time.LocalTime;

public interface StudentStatsService {
    void recordOrUpdateLearning(TaskScheduleRecord taskScheduleRecord, LocalTime startTime);

    void deleteRecord(TaskScheduleRecord taskScheduleRecord);

    StudentStats getStudentStat(Long studentId, LocalDate today);
}
