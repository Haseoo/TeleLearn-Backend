package kielce.tu.weaii.telelearn.views.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.views.TeacherView;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseBriefView {
    private final Long id;
    private final String name;
    private final TeacherView owner;
    @JsonProperty("isAutoAccept")
    private final boolean autoAccept;

    public static CourseBriefView from(Course model) {
        return new CourseBriefView(model.getId(),
                model.getName(),
                TeacherView.from(model.getOwner(), false),
                model.isAutoAccept());
    }
}
