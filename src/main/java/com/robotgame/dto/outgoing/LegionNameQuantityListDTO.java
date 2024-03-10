package com.robotgame.dto.outgoing;

import com.robotgame.domain.Legion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LegionNameQuantityListDTO {
    private String legionName;
    private Long unitQuantity;

    public LegionNameQuantityListDTO(Legion legion) {
        this.legionName = legion.getType().getDisplayName();
        this.unitQuantity = legion.getQuantity();
    }
}
