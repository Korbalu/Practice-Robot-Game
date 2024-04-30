package com.robotgame.dto.outgoing;

import com.robotgame.domain.Log;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class LogListDTO {
    private String headers;
    private String log;
    private LocalDateTime createdAt;

    public LogListDTO(Log log) {
        this.headers = log.getHeader();
        this.log = log.getLog();
        this.createdAt = log.getCreatedAt();
    }

}
