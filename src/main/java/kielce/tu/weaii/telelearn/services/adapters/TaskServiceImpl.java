package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.courses.PathWouldHaveCycle;
import kielce.tu.weaii.telelearn.exceptions.courses.TaskNotFound;
import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.models.UserRole;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.repositories.ports.TaskRepository;
import kielce.tu.weaii.telelearn.requests.courses.TaskRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.services.ports.CourseService;
import kielce.tu.weaii.telelearn.services.ports.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserServiceDetailsImpl userServiceDetails;
    private final CourseService courseService;

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
        for(Task pTask: task.getPreviousTasks()) {
            pTask.getPreviousTasks().remove(task);
            taskRepository.save(pTask);
        }
        for(Task nTask: task.getNextTasks()) {
            nTask.getPreviousTasks().remove(task);
            taskRepository.save(nTask);
        }
        taskRepository.delete(task);
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

    /*private void checkNewTask(Task task) {
        List<Long> ids = new ArrayList<>();
        ids.add(task.getId());
        if (checkCycle(task, ids)) {
            throw new PathWouldHaveCycle();
        }
    }*/

    /*private boolean checkCycle(Task currentTask, List<Long> ids) {
        if (currentTask.getPreviousTasks().isEmpty()) {
            return false;
        }
        boolean hasCycle = false;
        for (Task previousTask : currentTask.getPreviousTasks()) {
            if (ids.contains(previousTask.getId())) {
                return true;
            }
            hasCycle = hasCycle || checkCycle(previousTask, ids);
        }
        //ids.add(currentTask.getId());
        return hasCycle;
    }*/

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
