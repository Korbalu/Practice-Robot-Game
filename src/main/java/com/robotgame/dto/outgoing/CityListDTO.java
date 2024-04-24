package com.robotgame.dto.outgoing;

import com.robotgame.domain.City;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CityListDTO {
    private Long id;
    private String ownerName;
    private String race;
    private Long score;
    private Long x;
    private Long y;

    public CityListDTO(City city) {
        this.id = city.getId();
        this.ownerName = city.getOwner().getName();
        this.race = city.getRace().getDisplayName();
        this.score = city.getScore();
        this.x = city.getX();
        this.y = city.getY();
    }
}
