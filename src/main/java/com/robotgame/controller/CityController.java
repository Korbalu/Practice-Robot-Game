package com.robotgame.controller;

import com.robotgame.dto.incoming.BuildingCreationDTO;
import com.robotgame.dto.incoming.CityCreationDTO;
import com.robotgame.dto.outgoing.*;
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

    @PutMapping("/build")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> buildingBuilder(@RequestBody BuildingCreationDTO building){
        cityService.builder(building.getBuilding());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/buildings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<BuildingListDTO>> buildingLister(){
        return new ResponseEntity<>(cityService.buildingLister(), HttpStatus.OK);
    }

    @GetMapping("/allBuildings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<AllBuildingsListDTO>> allBuildings() {
        return new ResponseEntity<>(cityService.everyBuildingsLister(), HttpStatus.OK);
    }

    @GetMapping("/newTurn")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> takeTurns(){
        cityService.newTurn();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/allCities")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<CityListDTO>> cityLister(){
        return new ResponseEntity<>(cityService.cityLister(), HttpStatus.OK);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<LoggedInUserDetailsDTO> userDetailer(){
        return new ResponseEntity<>(cityService.userDetailer(), HttpStatus.OK);
    }

    @GetMapping("/autoCity")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SUPERUSER')")
    public ResponseEntity<Void> createAutoCity(){
        cityService.autoUserCityCreator();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
