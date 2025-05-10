package web.meta.wave.model;

public enum UserRole {
    ADMIN, USER, BANNED;

    @Override
    public String toString() {
        return "ROLE_" + name();
    }
}
