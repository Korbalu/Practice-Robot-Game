package com.robotgame.dto.incoming;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LegionCreationDTO {
    private String unit;
    private Long quantity;
}
