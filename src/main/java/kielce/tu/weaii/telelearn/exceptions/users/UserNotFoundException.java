package kielce.tu.weaii.telelearn.exceptions.users;

import kielce.tu.weaii.telelearn.exceptions.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(Long id) {
        super("użytkownik", id);
    }

    public UserNotFoundException(String loginOrEmail) {
        super(String.format("Użytkownik z loginem lub emailem %s nie został znaleziony.", loginOrEmail));
    }
}
