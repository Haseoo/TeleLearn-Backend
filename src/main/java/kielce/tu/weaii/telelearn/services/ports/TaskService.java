package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.requests.courses.TaskRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TaskService {
    Task getById(Long id);

    Task add(TaskRequest request, List<MultipartFile> attachments) throws IOException;

    Task update(Long id, TaskRequest request, List<MultipartFile> attachmentsToUpload) throws IOException;

    void delete(Long id);
}
