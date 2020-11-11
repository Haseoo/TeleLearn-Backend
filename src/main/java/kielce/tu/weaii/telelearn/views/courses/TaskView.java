package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.models.courses.Task;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class TaskView {
    Long id;
    String name;
    String description;
    int learningTimeHours;
    int learningTimeMinutes;
    LocalDate dueDate;
    Long pathId;
    List<Long> previousTaskIds;
    List<Long> nextTaskIds;
    List<AttachmentView> attachmentViews;

    public static TaskView from(Task model) {
        return new TaskView(model.getId(),
                model.getName(),
                model.getDescription(),
                model.getLearningTimeHours(),
                model.getLearningTimeMinutes(),
                model.getDueDate(),
                model.getPath().getId(),
                model.getPreviousTasks().stream().map(Task::getId).collect(Collectors.toList()),
                model.getNextTasks().stream().map(Task::getId).collect(Collectors.toList()),
                model.getAttachments().stream().map(AttachmentView::form).collect(Collectors.toList()));
    }
}
