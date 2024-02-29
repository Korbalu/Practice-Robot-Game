package com.robotgame.controller;

import com.robotgame.dto.incoming.BuildingCreationDTO;
import com.robotgame.dto.incoming.CityCreationDTO;
import com.robotgame.dto.outgoing.BuildingListDTO;
import com.robotgame.dto.outgoing.CityDetailsDTO;
import com.robotgame.dto.outgoing.RaceNameDTO;
import com.robotgame.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city")
public class CityController {

    private CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CityDetailsDTO> cityDetailer(){
        return new ResponseEntity<>(cityService.cityDetailer(), HttpStatus.OK);
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> cityCreator(@RequestBody CityCreationDTO CCDTO){
        cityService.cityCreator(CCDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/races")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<RaceNameDTO>> raceLister(){
        return new ResponseEntity<>(cityService.raceLister(), HttpStatus.OK);
    }

    @PostMapping("/build")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> buildingBuilder(@RequestBody BuildingCreationDTO building){
        cityService.builder(building.getBuilding());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/buildings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<BuildingListDTO>> buildingLister(){
        return new ResponseEntity<>(cityService.buildingLister(), HttpStatus.OK);
    }
}
