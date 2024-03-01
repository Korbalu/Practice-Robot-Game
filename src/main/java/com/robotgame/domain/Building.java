package com.robotgame.domain;

import lombok.Getter;

@Getter
public enum Building {
    CRYSTALMINE("Crystalmine", 15, 75, 10),
    WALL("Wall", 5, 60, 15),
    FACTORY("Factory", 2, 100, 12);

    private String displayName;
    private int production;
    private int cost;
    private int score;

    Building(String displayName, int production, int cost, int score) {
        this.displayName = displayName;
        this.production = production;
        this.cost = cost;
        this.score = score;
    }
}
