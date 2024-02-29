package com.robotgame.controller;

import com.robotgame.dto.incoming.LegionCreationDTO;
import com.robotgame.dto.outgoing.LegionListDTO;
import com.robotgame.service.ArmyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/army")
public class ArmyController {
    private ArmyService armyService;

    public ArmyController(ArmyService armyService) {
        this.armyService = armyService;
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ResponseEntity<Void> unitAdder(@RequestBody LegionCreationDTO LCDTO) {
        armyService.increaseUnit(LCDTO.getUnit(), LCDTO.getQuantity());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ResponseEntity<List<LegionListDTO>> armyLister(){
        return new ResponseEntity<>(armyService.armyLister(), HttpStatus.OK);
    }
}
