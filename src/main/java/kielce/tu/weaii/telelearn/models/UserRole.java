package kielce.tu.weaii.telelearn.models;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    TEACHER("ROLE_TEACHER"),
    STUDENT("ROLE_STUDENT");

    private String string;

    UserRole(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
