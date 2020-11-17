package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.models.courses.Path;
import lombok.Value;

@Value
public class PathBriefView {
    Long id;
    String name;
    String description;
    Long courseId;

    /*public static PathBriefView from(Path model) {
        return new PathBriefView(model.getId(),
                model.getName(),
                model.getDescription(),
                model.getCourse().getId());
    }*/
}
