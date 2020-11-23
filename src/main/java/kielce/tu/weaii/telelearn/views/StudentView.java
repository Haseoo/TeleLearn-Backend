package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.UserRole;
import lombok.Value;

@Value
public class StudentView {
    Long id;
    String username;
    String email;
    String name;
    String surname;
    UserRole userRole;
    boolean enabled;
    String unit;
    TimeVew dailyLearningTime;

    public static StudentView from(Student model, boolean loginPermitted) {
        return new StudentView(model.getId(),
                (loginPermitted) ? model.getUsername() : null,
                model.getEmail(),
                model.getName(),
                model.getSurname(),
                model.getUserRole(),
                model.isEnabled(),
                model.getUnit(),
                TimeVew.form(model.getDailyLearningTime()));
    }
}
