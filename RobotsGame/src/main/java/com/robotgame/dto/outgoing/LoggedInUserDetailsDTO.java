package com.robotgame.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoggedInUserDetailsDTO {
    private Long userId;
    private Long cityId;
    private String userName;
    private String cityName;
    private Long vault;
    private Long score;
}
