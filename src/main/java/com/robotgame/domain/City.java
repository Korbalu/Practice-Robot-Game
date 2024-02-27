package com.robotgame.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Data
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cityId")
    private Long id;
    @Column(name = "cityName")
    private String name;
    @Column
    private Long vault;
    @Column
    private Long score;
    @Column
    private Long area;
    @Enumerated(EnumType.STRING)
    private Race race;
    @ElementCollection
    private Map<Building, Long> buildings;
    @OneToOne
    @JoinColumn(name = "owner_id")
    private CustomUser owner;
}
