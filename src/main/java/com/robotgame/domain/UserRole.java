package com.robotgame.domain;

public enum UserRole {
    USER("User"),
    SUPERUSER("SuperUser");

    private String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
