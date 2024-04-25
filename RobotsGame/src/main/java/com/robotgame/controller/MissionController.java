package com.robotgame.controller;

import com.robotgame.dto.incoming.CityCreationDTO;
import com.robotgame.dto.incoming.MissionExpeditionDTO;
import com.robotgame.service.MissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mission")
public class MissionController {
    private MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @PostMapping("/expedition")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> cityCreator(@RequestBody MissionExpeditionDTO missionExpeditionDTO){
        missionService.startExpedition(missionExpeditionDTO.getQuantity());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
