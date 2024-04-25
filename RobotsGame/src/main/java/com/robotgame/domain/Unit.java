package com.robotgame.domain;

import lombok.Getter;

@Getter
public enum Unit {
    LightBot("LightBot", 15, 5, 15, "Laser", "Laser", "None", true, 20,3),
    LightBotUpg("LightBotUpg", 20, 10, 15, "Laser", "Laser", "None", false, 40,5),
    HeavyHitter("HeavyHitter", 20, 10, 20, "Cannon", "Laser", "None", true, 50,5),
    Bomber("Bomber", 25, 5, 10, "Rocket", "Cannon", "None", true, 40,4),
    HumanSniper("HumanSniper", 25, 10, 10, "Laser", "Rocket", "Human", true, 60,6),
    ExoquianMortarer("ExoquianMortarer", 20, 15, 10, "Cannon", "Laser", "Exoquian", true, 60,6),
    NebulaeBlaster("NebulaeBlaster", 30, 10, 5, "Rocket", "Cannon", "Nebulae", true, 60,6);

    private String displayName;
    private Integer attack;
    private Integer armor;
    private Integer structure;
    private String attackType;
    private String armorType;
    private String raceConnect;
    private boolean buyable;
    private Integer cost;
    private Integer score;

    Unit(String displayName, Integer attack, Integer armor, Integer structure,
         String attackType, String armorType, String raceConnect, boolean buyable, Integer cost, Integer score) {
        this.displayName = displayName;
        this.attack = attack;
        this.armor = armor;
        this.structure = structure;
        this.attackType = attackType;
        this.armorType = armorType;
        this.raceConnect = raceConnect;
        this.buyable = buyable;
        this.cost = cost;
        this.score = score;
    }
}
