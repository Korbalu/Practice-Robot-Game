package com.robotgame.domain;

import lombok.Getter;

@Getter
public enum Building {
    CRYSTALMINE("Crystalmine", 10, 75, 10),
    WALL("Wall", 2, 60, 15),
    FACTORY("Factory", 2, 100, 12),
    LIGHTREPELLER("Lightrepeller", 3, 60, 15),
    MANUFACTUREPLANT("Manufactureplant", 1, 100, 13),
    SPECIALIZER("Specializer", 2, 200, 25);

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
