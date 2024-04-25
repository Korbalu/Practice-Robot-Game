package com.robotgame.domain;

import lombok.Getter;

@Getter
public enum Race {
    EXOQUIAN("Exoquian", "Industrial"),
    HUMAN("Human", "Builder"),
    NEBULAE("Nebulae", "Fighter");

    private String displayName;
    private String orientation;

    Race(String displayName, String orientation) {
        this.displayName = displayName;
        this.orientation = orientation;
    }
}
