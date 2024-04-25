package com.robotgame.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityCreationDTO {
    private String name;
    private String race;
}
