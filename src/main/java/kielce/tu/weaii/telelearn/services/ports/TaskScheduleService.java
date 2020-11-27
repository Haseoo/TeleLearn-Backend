package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.requests.TimeSpanRequest;
import kielce.tu.weaii.telelearn.requests.courses.RecordLearningRequest;
import kielce.tu.weaii.telelearn.requests.courses.ScheduleTaskRequest;
import kielce.tu.weaii.telelearn.requests.courses.ScheduleUpdateRequest;

import java.time.LocalDate;
import java.util.List;

public interface TaskScheduleService {
    TaskScheduleRecord getById(Long id);

    List<TaskScheduleRecord> getListForStudent(Long studentId);

    List<TaskScheduleRecord> getListForTaskAndStudent(Long studentId, Long taskId);

    TaskScheduleRecord schedule(ScheduleTaskRequest request, LocalDate today);

    TaskScheduleRecord updateSchedule(Long id, ScheduleUpdateRequest request, LocalDate today);

    TaskScheduleRecord updateLearningTime(Long id, RecordLearningRequest request, LocalDate today);

    void delete(Long id);

    void deleteSchedulesForStudent(Long studentId);
}
