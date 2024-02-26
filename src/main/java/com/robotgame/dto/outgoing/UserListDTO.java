package com.robotgame.dto.outgoing;

import com.robotgame.domain.CustomUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserListDTO {
    private String name;
    private LocalDateTime creation;

    public UserListDTO(CustomUser user) {
        this.name = user.getName();
        this.creation = user.getCreatedAt();
    }
}
