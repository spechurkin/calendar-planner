package me.proj.entities;

public enum UserRole {
    PLAYER,
    MASTER,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
