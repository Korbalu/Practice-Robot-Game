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
    private Long freeArea;
    private Long score;
    private Integer turns;
    private String ownerName;
    private Long resSent;
    private Long x;
    private Long y;
}
