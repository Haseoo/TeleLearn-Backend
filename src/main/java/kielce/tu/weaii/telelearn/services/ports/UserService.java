package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.requests.LoginRequest;
import kielce.tu.weaii.telelearn.requests.UserPasswordPatchRequest;
import kielce.tu.weaii.telelearn.security.JwtAuthenticationResponse;

import java.util.List;

public interface UserService {
    void checkAvailability(String email);

    void checkAvailability(String login, String email);

    JwtAuthenticationResponse getJwt(LoginRequest loginRequest);

    User getUserByLoginOrEmail(String loginOrEmail);

    List<User> getList();

    User getById(Long id);

    User updatePassword(Long id, UserPasswordPatchRequest request);

    void delete(Long id);

    boolean isCurrentUserOrAdmin(Long userId);

    void verifyPermissionToUser(Long userId);
}
