package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.requests.courses.TaskProgressPatchRequest;
import kielce.tu.weaii.telelearn.requests.courses.TaskRepeatPatchRequest;
import kielce.tu.weaii.telelearn.requests.courses.TaskRequest;
import kielce.tu.weaii.telelearn.servicedata.TaskStudentSummary;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface TaskService {
    Task getById(Long id);

    Task add(TaskRequest request, List<MultipartFile> attachments) throws IOException;

    Task update(Long id, TaskRequest request, List<MultipartFile> attachmentsToUpload) throws IOException;

    void delete(Long id);

    Task updateProgress(Long id, TaskProgressPatchRequest request);

    Task updateTaskRepeat(Long id, TaskRepeatPatchRequest request);

    TaskStudentSummary getStudentByTasksFromCurse(Long studentId, LocalDate today);
}
