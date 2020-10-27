package kielce.tu.weaii.telelearn.repositories.ports;

import kielce.tu.weaii.telelearn.models.User;

import java.util.Optional;

public interface UserRepository extends BaseCRUDRepository<User> {
    Optional<User> getUserByLoginOrEmail(String loginOrEmail);

    Optional<User> getUserByLogin(String login);

    Optional<User> getUserByEmail(String email);
}
