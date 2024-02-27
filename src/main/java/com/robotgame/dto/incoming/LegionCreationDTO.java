package com.robotgame.dto.incoming;

import com.robotgame.domain.Race;
import com.robotgame.domain.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegionCreationDTO {
    private Unit type;
    private Long quantity;
    private Race race;
}
