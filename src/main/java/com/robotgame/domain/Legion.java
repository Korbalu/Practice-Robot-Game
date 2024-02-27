package com.robotgame.domain;

import com.robotgame.dto.incoming.LegionCreationDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Legion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "legionId")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "legionType")
    private Unit type;
    private Long quantity;
    @Enumerated(EnumType.STRING)
    private Race race;
    @ManyToOne
    private CustomUser owner;

    public Legion(LegionCreationDTO LCDTO) {
        this.type = LCDTO.getType();
        this.quantity = LCDTO.getQuantity();
        this.race = LCDTO.getRace();
    }
}
