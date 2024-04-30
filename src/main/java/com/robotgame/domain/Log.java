package com.robotgame.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String header;
    private String log;
    private LocalDateTime createdAt;
    @ManyToOne
    private CustomUser owner;

    public Log(String header, String log, CustomUser owner) {
        this.header = header;
        this.log = log;
        this.createdAt = LocalDateTime.now();
        this.owner = owner;
    }

}
