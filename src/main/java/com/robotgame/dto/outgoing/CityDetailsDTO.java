package com.robotgame.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityDetailsDTO {
    private String name;
    private String race;
    private Long vault;
    private Long area;
    private Long score;
    private String ownerName;
}
