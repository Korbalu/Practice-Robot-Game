package com.robotgame.dto.outgoing;

import com.robotgame.domain.Legion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegionListDTO {
    private String type;
    private Long quantity;
    private String race;

    public LegionListDTO(Legion legion) {
        this.type = legion.getType().getDisplayName();
        this.quantity = legion.getQuantity();
        this.race = legion.getRace().getDisplayName();
    }
}
