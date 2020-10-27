package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.models.UserRole;
import kielce.tu.weaii.telelearn.security.JwtAuthenticationResponse;
import lombok.Value;

@Value
public class UserLoginResponse {
    String token;
    Long id;
    String login;
    String name;
    String surname;
    UserRole userRole;

    public static UserLoginResponse of(JwtAuthenticationResponse jwtAuthenticationResponse, User user) {
        return new UserLoginResponse(jwtAuthenticationResponse.getTokenType() + " " + jwtAuthenticationResponse.getAccessToken(),
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                user.getUserRole());
    }
}
