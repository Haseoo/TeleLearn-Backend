package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.UserRole;
import lombok.Value;

@Value
public class TeacherView {
    Long id;
    String username;
    String email;
    String name;
    String surname;
    UserRole userRole;
    boolean enabled;
    String unit;
    String title;

    public static TeacherView from(Teacher model, boolean loginPermitted) {
        return new TeacherView(model.getId(),
                (loginPermitted) ? model.getUsername() : null,
                model.getEmail(),
                model.getName(),
                model.getSurname(),
                model.getUserRole(),
                model.isEnabled(),
                model.getUnit(),
                model.getTitle());
    }
}
