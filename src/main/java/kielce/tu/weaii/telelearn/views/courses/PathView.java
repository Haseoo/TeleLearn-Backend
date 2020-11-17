package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.models.courses.Path;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class PathView {
    Long id;
    String name;
    String description;
    Long courseId;
    List<TaskView> taskViews;

    /*public static PathView from(Path model) {
        return new PathView(model.getId(),
                model.getName(),
                model.getDescription(),
                model.getCourse().getId(),
                model.getTasks().stream().map(TaskView::from).collect(Collectors.toList()));
    }*/
}
