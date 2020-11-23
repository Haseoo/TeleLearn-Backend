package kielce.tu.weaii.telelearn.views.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.models.courses.TaskStudent;
import kielce.tu.weaii.telelearn.views.TimeVew;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TaskViewForStudent extends TaskView {

    public TaskViewForStudent(Long id,
                              String name,
                              String description,
                              TimeVew learningTime,
                              LocalDate dueDate,
                              Long courseId,
                              List<TaskBriefView> previousTasks,
                              List<TaskBriefView> nextTasks,
                              List<AttachmentView> attachments,
                              int taskCompletion,
                              boolean toRepeat) {
        super(id, name, description, learningTime, dueDate, courseId, previousTasks, nextTasks, attachments);
        this.taskCompletion = taskCompletion;
        this.toRepeat = toRepeat;
    }

    private final int taskCompletion;

    @JsonProperty("isToRepeat")
    private final boolean toRepeat;


    public static TaskViewForStudent from(Task model, Long studentId) {
        TaskStudent ts = model.getStudentRecordOrNull(studentId);
        return new TaskViewForStudent(model.getId(),
                model.getName(),
                model.getDescription(),
                TimeVew.form(model.getLearningTime()),
                model.getDueDate(),
                model.getCourse().getId(),
                model.getPreviousTasks().stream().map(TaskBriefView::from).collect(Collectors.toList()),
                model.getNextTasks().stream().map(TaskBriefView::from).collect(Collectors.toList()),
                model.getAttachments().stream().map(AttachmentView::form).collect(Collectors.toList()),
                (ts != null) ? ts.getTaskCompletion() : 0,
                (ts != null) && ts.isToRepeat());

    }
}
