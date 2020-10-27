package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.models.UserRole;
import lombok.Value;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Value
public class UserView {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    String username;
    String email;
    String name;
    String surname;
    UserRole userRole;
    boolean enabled;


    public static UserView from(User model, boolean loginPermitted) {
        return new UserView(model.getId(),
                (loginPermitted) ? model.getUsername() : null,
                model.getEmail(),
                model.getName(),
                model.getSurname(),
                model.getUserRole(),
                model.isEnabled());
    }
}
