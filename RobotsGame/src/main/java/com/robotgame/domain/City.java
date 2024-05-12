package com.robotgame.domain;

import com.robotgame.dto.incoming.CityCreationDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
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
    private Long freeArea;
    @Column
    private Long x;
    @Column
    private Long y;
    @Enumerated(EnumType.STRING)
    private Race race;
    @ElementCollection
    private Map<Building, Long> buildings;
    @OneToOne
    @JoinColumn(name = "owner_id")
    private CustomUser owner;

    public City(CityCreationDTO CCDTO) {
        this.name = CCDTO.getName();
        this.vault = 1000L;
        this.score = 0L;
        this.area = 50L;
        this.freeArea = this.area;
        this.buildings = new HashMap<>();
    }

    public City(String name, Race race, CustomUser owner) {
        this.name = name;
        this.vault = 1000L;
        this.score = 0L;
        this.race = race;
        this.area = 50L;
        this.freeArea = this.area;
        this.owner = owner;
        this.buildings = new HashMap<>();
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vault=" + vault +
                ", owner=" + owner.getName() +
                '}';
    }
}
