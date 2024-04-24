package com.robotgame.domain;

public enum UserRole {
    ROLE_USER("ROLE_USER"),
    ROLE_SUPERUSER("ROLE_SUPERUSER");

    private String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
