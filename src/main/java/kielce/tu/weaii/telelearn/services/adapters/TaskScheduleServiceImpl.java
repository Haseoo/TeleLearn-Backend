package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.courses.*;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.models.courses.TaskStudent;
import kielce.tu.weaii.telelearn.repositories.ports.TaskScheduleRepository;
import kielce.tu.weaii.telelearn.requests.courses.RecordLearningRequest;
import kielce.tu.weaii.telelearn.requests.courses.ScheduleTaskRequest;
import kielce.tu.weaii.telelearn.requests.courses.TimeSpanRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.services.ports.StudentService;
import kielce.tu.weaii.telelearn.services.ports.StudentStatsService;
import kielce.tu.weaii.telelearn.services.ports.TaskScheduleService;
import kielce.tu.weaii.telelearn.services.ports.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static kielce.tu.weaii.telelearn.utilities.Constants.TASK_COMPLETED;

@Service
@RequiredArgsConstructor
public class TaskScheduleServiceImpl implements TaskScheduleService {
    private final TaskScheduleRepository repository;
    private final UserServiceDetailsImpl userServiceDetails;
    private final TaskService taskService;
    private final StudentService studentService;

    @Autowired
    private StudentStatsService studentStatsService;

    @Override
    public TaskScheduleRecord getById(Long id) {
        TaskScheduleRecord record = repository.getById(id).orElseThrow(ScheduleRecordNotFound::new);
        if (!record.getStudent().getId().equals(userServiceDetails.getCurrentUser().getId())) {
            throw new AuthorizationException("Wpis planu użytkownika", userServiceDetails.getCurrentUser().getId(), id);
        }
        return record;
    }

    @Override
    public List<TaskScheduleRecord> getListForStudent(Long studentId) {
        if (!studentId.equals(userServiceDetails.getCurrentUser().getId())) {
            throw new AuthorizationException("Plan użytkownika", userServiceDetails.getCurrentUser().getId(), studentId);
        }
        return repository.getAll().stream().filter(entry -> entry.getStudent().getId().equals(studentId)).collect(Collectors.toList());
    }

    @Override
    public List<TaskScheduleRecord> getListForTaskAndStudent(Long studentId, Long taskId) {
        List<TaskScheduleRecord> studentSchedule = getListForStudent(studentId);
        return studentSchedule.stream().filter(record -> record.getTask().getId().equals(taskId)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskScheduleRecord schedule(ScheduleTaskRequest request, LocalDate today) {
        if (request.getDate().isBefore(today)) {
            throw new ScheduleForPastNotPossible();
        }
        TaskScheduleRecord record = new TaskScheduleRecord();
        BeanUtils.copyProperties(request, record);
        record.setLearningTime(Duration.ZERO);
        record.setStudent(studentService.getById(request.getStudentId()));
        record.setTask(taskService.getById(request.getTaskId()));
        return repository.save(record);
    }

    @Override
    @Transactional
    public TaskScheduleRecord updatePlannedTime(Long id, TimeSpanRequest request, LocalDate today) {
        TaskScheduleRecord record = getById(id);
        if (record.getDate().isBefore(today)) {
            throw new SchedulePlannedTimeUpdateNotPossible();
        }
        record.setPlannedTime(request.getTimeSpan());
        return repository.save(record);
    }

    @Override
    @Transactional
    public TaskScheduleRecord updateLearningTime(Long id, RecordLearningRequest request, LocalDate today) {
        TaskScheduleRecord record = getById(id);
        if (!record.getDate().isEqual(today)) {
            throw new UpdateLearningTimeNotPossible();
        }
        for (Task pTask : record.getTask().getPreviousTasks()) {
            TaskStudent taskStudent = pTask.getStudentRecordOrNull(record.getStudent().getId());
            if (taskStudent == null || taskStudent.getTaskCompletion() != TASK_COMPLETED) {
                throw new PreviousTaskNotCompleted();
            }
        }
        record.setLearningTime(request.getDuration().getTimeSpan());
        record = repository.save(record);
        studentStatsService.recordOrUpdateLearning(record, request.getStartTime());
        return record;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TaskScheduleRecord record = getById(id);
        studentStatsService.deleteRecord(record);
        repository.delete(record);
    }

    @Override
    @Transactional
    public void deleteSchedulesForStudent(Long studentId) {
        repository.deleteAllStudentRecord(studentId);
    }
}
