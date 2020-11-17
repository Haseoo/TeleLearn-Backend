package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.models.courses.Task;
import lombok.Value;

@Value
public class TaskBriefView {
    Long id;
    String name;

    public static TaskBriefView from(Task model) {
        return new TaskBriefView(model.getId(), model.getName());
    }
}
