package com.robotgame.domain;

import lombok.Getter;

@Getter
public enum Unit {
    LightBot("LightBot", 15, 5, 15, "Laser", "Laser", 20,3),
    HeavyHitter("HeavyHitter", 20, 10, 20, "Cannon", "Laser", 50,5),
    Bomber("Bomber", 25, 5, 10, "Rocket", "Cannon", 40,4);

    private String displayName;
    private Integer attack;
    private Integer armor;
    private Integer structure;
    private String attackType;
    private String armorType;
    private Integer cost;
    private Integer score;

    Unit(String displayName, Integer attack, Integer armor, Integer structure,
         String attackType, String armorType, Integer cost, Integer score) {
        this.displayName = displayName;
        this.attack = attack;
        this.armor = armor;
        this.structure = structure;
        this.attackType = attackType;
        this.armorType = armorType;
        this.cost = cost;
        this.score = score;
    }
}
