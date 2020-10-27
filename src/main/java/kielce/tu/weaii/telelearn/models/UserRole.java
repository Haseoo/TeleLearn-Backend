package kielce.tu.weaii.telelearn.models;

public enum UserRole {
    ADMIN("ROLE_ADMIN", "administrator"),
    TEACHER("ROLE_TEACHER", "nauczyciel"),
    STUDENT("ROLE_STUDENT", "student");

    private String string;
    private String type;

    UserRole(String string, String type) {
        this.string = string;
        this.type = type;
    }

    @Override
    public String toString() {
        return string;
    }

    public String getTypeDescription() {
        return type;
    }
}
