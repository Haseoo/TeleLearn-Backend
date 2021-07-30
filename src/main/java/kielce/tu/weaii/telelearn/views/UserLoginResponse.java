package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.models.UserRole;
import lombok.Value;

@Value
public class UserLoginResponse {
    Long id;
    String login;
    String name;
    String surname;
    UserRole userRole;

    public static UserLoginResponse of(User user) {
        return new UserLoginResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                user.getUserRole());
    }
}
