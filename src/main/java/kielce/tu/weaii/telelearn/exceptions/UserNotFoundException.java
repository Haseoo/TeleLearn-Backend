package kielce.tu.weaii.telelearn.exceptions;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(Long id) {
        super("user", id);
    }

    public UserNotFoundException(String loginOrEmail) {
        super(String.format("User with login or email %s not found.", loginOrEmail));
    }
}
