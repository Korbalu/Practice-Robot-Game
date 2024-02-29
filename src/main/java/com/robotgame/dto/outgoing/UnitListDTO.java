package com.robotgame.dto.outgoing;

import com.robotgame.domain.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UnitListDTO {
    private String displayName;
    private Integer attack;
    private Integer armor;
    private Integer structure;
    private String attackType;
    private String armorType;
    private Integer cost;

    public UnitListDTO(Unit unit) {
        this.displayName = unit.getDisplayName();
        this.attack = unit.getAttack();
        this.armor = unit.getArmor();
        this.structure = unit.getStructure();
        this.attackType = unit.getAttackType();
        this.armorType = unit.getArmorType();
        this.cost = unit.getCost();
    }
}
