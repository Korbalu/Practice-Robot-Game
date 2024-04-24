package com.robotgame.dto.outgoing;

import com.robotgame.domain.Race;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RaceNameDTO {
    private String raceName;

    public RaceNameDTO(Race race) {
        this.raceName = race.getDisplayName();
    }
}
