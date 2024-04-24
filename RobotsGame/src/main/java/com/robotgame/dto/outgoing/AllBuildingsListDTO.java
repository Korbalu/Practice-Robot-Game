package com.robotgame.dto.outgoing;

import com.robotgame.domain.Building;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AllBuildingsListDTO {
    private String displayName;
    private Integer production;
    private Integer cost;

    public AllBuildingsListDTO(Building building) {
        this.displayName = building.getDisplayName();
        this.production = building.getProduction();
        this.cost = building.getCost();
    }
}
