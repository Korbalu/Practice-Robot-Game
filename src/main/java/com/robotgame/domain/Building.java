package com.robotgame.domain;

import lombok.Getter;

@Getter
public enum Building {
    CRYSTALMINE("Crystalmine", 15, 75),
    WALL("Wall", 5, 60),
    FACTORY("Factory", 2, 100);

    private String displayName;
    private Integer production;
    private Integer cost;

    Building(String displayName, Integer production, Integer cost) {
        this.displayName = displayName;
        this.production = production;
        this.cost = cost;
    }
}
