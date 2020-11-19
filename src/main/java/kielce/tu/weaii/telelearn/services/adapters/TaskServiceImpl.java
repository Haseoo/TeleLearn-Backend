package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.courses.PathWouldHaveCycle;
import kielce.tu.weaii.telelearn.exceptions.courses.TaskMustBeCompleted;
import kielce.tu.weaii.telelearn.exceptions.courses.TaskNotFound;
import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.models.UserRole;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.models.courses.TaskStudent;
import kielce.tu.weaii.telelearn.repositories.ports.TaskRepository;
import kielce.tu.weaii.telelearn.requests.courses.TaskProgressPatchRequest;
import kielce.tu.weaii.telelearn.requests.courses.TaskRepeatPatchRequest;
import kielce.tu.weaii.telelearn.requests.courses.TaskRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.servicedata.TaskScheme;
import kielce.tu.weaii.telelearn.servicedata.TaskSchemeRecord;
import kielce.tu.weaii.telelearn.services.ports.CourseService;
import kielce.tu.weaii.telelearn.services.ports.StudentService;
import kielce.tu.weaii.telelearn.services.ports.TaskService;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserServiceDetailsImpl userServiceDetails;
    private final UserService userService;
    private final CourseService courseService;
    private final StudentService studentService;

    @Override
    public Task getById(Long id) {
        Task task = taskRepository.getById(id).orElseThrow(() -> new TaskNotFound(id));
        User currentUser = userServiceDetails.getCurrentUser();
        if ((currentUser.getUserRole().equals(UserRole.TEACHER) &&
                !task.getCourse().getOwner().getId().equals(currentUser.getId())) ||
                (currentUser.getUserRole().equals(UserRole.STUDENT) &&
                        task.getCourse().getStudents().stream()
                                .noneMatch(entry -> entry.getStudent().getId().equals(currentUser.getId())))) {
            throw new AuthorizationException("zadanie", currentUser.getId(), id);
        }
        return task;
    }

    @Override
    @Transactional
    public Task add(TaskRequest request, List<MultipartFile> attachments) throws IOException {
        Task task = new Task();
        LocalDateTime now = LocalDateTime.now();
        BeanUtils.copyProperties(request, task);
        task.setCourse(courseService.getById(request.getCourseId()));
        task.setLearningTime(Duration.ofMinutes(request.getLearningTimeMinutes()).plus(Duration.ofHours(request.getLearningTimeHours())));
        task.setAttachments(prepareAttachments(attachments, now, task));
        task.setPreviousTasks(getPreviousTasks(request));
        checkNewTask(task);
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task update(Long id, TaskRequest request, List<MultipartFile> attachmentsToUpload) throws IOException {
        Task task = getById(id);
        if (!task.getCourse().getId().equals(request.getCourseId())) {
            task.setCourse(courseService.getById(request.getCourseId()));
        }
        BeanUtils.copyProperties(request, task);
        task.setLearningTime(Duration.ofMinutes(request.getLearningTimeMinutes()).plus(Duration.ofHours(request.getLearningTimeHours())));
        task.setPreviousTasks(getPreviousTasks(request));
        checkNewTask(task);
        deleteAttachments(request, task);
        task.getAttachments().addAll(prepareAttachments(attachmentsToUpload, LocalDateTime.now(), task));
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Task task = getById(id);
        for (Task pTask : task.getPreviousTasks()) {
            pTask.getPreviousTasks().remove(task);
            taskRepository.save(pTask);
        }
        for (Task nTask : task.getNextTasks()) {
            nTask.getPreviousTasks().remove(task);
            taskRepository.save(nTask);
        }
        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public Task updateProgress(Long id, TaskProgressPatchRequest request) {
        if (!request.getStudentId().equals(userServiceDetails.getCurrentUser().getId())) {
            throw new AuthorizationException("Aktualizacja postępu zadania.", userServiceDetails.getCurrentUser().getId(), request.getStudentId());
        }
        Task task = getById(id);
        TaskStudent taskStudent = task.getStudentRecordOrNull(request.getStudentId());
        if (taskStudent != null) {
            taskStudent.setTaskCompletion(request.getProgress());
        } else {
            taskStudent = new TaskStudent();
            taskStudent.setTask(task);
            taskStudent.setStudent(studentService.getById(request.getStudentId()));
            taskStudent.setTaskCompletion(request.getProgress());
            task.getStudents().add(taskStudent);
        }
        if (taskStudent.getTaskCompletion() < 100) {
            taskStudent.setToRepeat(false);
        }
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateTaskRepeat(Long id, TaskRepeatPatchRequest request) {
        if (!request.getStudentId().equals(userServiceDetails.getCurrentUser().getId())) {
            throw new AuthorizationException("Ustawanie zadania do powtórzenia.", userServiceDetails.getCurrentUser().getId(), request.getStudentId());
        }
        Task task = getById(id);
        TaskStudent taskStudent = task.getStudentRecordOrNull(request.getStudentId());
        if (taskStudent == null || taskStudent.getTaskCompletion() != 100) {
            throw new TaskMustBeCompleted();
        }
        taskStudent.setToRepeat(request.getToRepeat());
        return taskRepository.save(task);
    }

    @Override
    public TaskScheme getStudentByTasksFromCurse(Long studentId, LocalDate today) {
        if (!userService.isCurrentUserOrAdmin(studentId)) {
            throw new AuthorizationException("lista zadań użytkownika", userServiceDetails.getCurrentUser().getId(), studentId);
        }
        TaskScheme taskScheme = new TaskScheme();
        List<Task> tasks = taskRepository.getStudentByTasksFromCurse(studentId);
        taskScheme.setDelayedTasks(
                tasks.stream()
                        .filter(task -> task.getDueDate().isBefore(today) &&
                                (task.getStudentRecordOrNull(studentId) == null
                                        || task.getStudentRecordOrNull(studentId).getTaskCompletion() != 100))
                        .map(task -> getTaskSchemeRecord(task, studentId, today))
                        .collect(Collectors.toList())
        );
        taskScheme.setTaskToRepeat(
                tasks.stream()
                        .filter(task -> task.getStudentRecordOrNull(studentId) != null && task.getStudentRecordOrNull(studentId).isToRepeat())
                        .map(task -> getTaskSchemeRecord(task, studentId, today))
                        .collect(Collectors.toList())
        );
        taskScheme.setTasksForDay(
                tasks.stream()
                        .filter(task -> !task.getDueDate().isBefore(today))
                        .filter(task -> task.getStudentRecordOrNull(studentId) == null ||
                                task.getStudentRecordOrNull(studentId).getTaskCompletion() != 100)
                        .map(task -> getTaskSchemeRecord(task, studentId, today))
                        .collect(Collectors.groupingBy(record -> record.getTask().getDueDate()))
        );

        return taskScheme;
    }

    private TaskSchemeRecord getTaskSchemeRecord(Task task, Long studentId, LocalDate today) {
        Duration totalLearningTime = task.getPlanRecords().stream()
                .filter(record -> record.getStudent().getId().equals(studentId))
                .map(TaskScheduleRecord::getLearningTime)
                .reduce(Duration.ZERO, Duration::plus);
        Duration totalPlannedLearningTime = task.getPlanRecords().stream()
                .filter(record -> record.getStudent().getId().equals(studentId))
                .filter(record -> !record.getDate().isBefore(today))
                .map(TaskScheduleRecord::getPlannedTime)
                .reduce(Duration.ZERO, Duration::plus);
        TaskSchemeRecord record = new TaskSchemeRecord();
        record.setTask(task);
        record.setTotalLearningTime(totalLearningTime);
        record.setTotalPlannedLearningTime(totalPlannedLearningTime);
        return record;
    }


    private List<Attachment> prepareAttachments(List<MultipartFile> attachments, LocalDateTime now, Task task) throws IOException {
        List<Attachment> attachmentList = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile file : attachments) {
                Attachment attachment = new Attachment();
                attachment.setFileName(file.getOriginalFilename());
                attachment.setFileType(file.getContentType());
                attachment.setUploadTime(now);
                attachment.setData(file.getBytes());
                attachmentList.add(attachment);
                attachment.setTask(task);
            }
        }
        return attachmentList;
    }

    private void checkNewTask(Task task) {
        if (checkCycle(task, task.getId())) {
            throw new PathWouldHaveCycle();
        }
    }

    private boolean checkCycle(Task currentTask, Long searchedTaskId) {
        if (currentTask.getPreviousTasks().isEmpty()) {
            return false;
        }
        boolean hasCycle = false;
        for (Task previousTask : currentTask.getPreviousTasks()) {
            if (previousTask.getId().equals(searchedTaskId)) {
                return true;
            }
            hasCycle = hasCycle || checkCycle(previousTask, searchedTaskId);
        }
        return hasCycle;
    }

    private List<Task> getPreviousTasks(TaskRequest request) {
        List<Task> previousTasks = new ArrayList<>();
        for (Long taskId : request.getPreviousTaskIds()) {
            previousTasks.add(getById(taskId));
        }
        return previousTasks;
    }

    private void deleteAttachments(TaskRequest request, Task task) {
        if (request.getAttachmentIdsToDelete() != null) {
            for (long attachmentId : request.getAttachmentIdsToDelete()) {
                task.getAttachments().removeIf(attachment -> {
                    if (attachment.getId() == null) {
                        return false;
                    }
                    return attachment.getId().equals(attachmentId);
                });
            }
        }
    }
}
