package com.robotgame.service;

import com.robotgame.domain.Building;
import com.robotgame.domain.City;
import com.robotgame.domain.CustomUser;
import com.robotgame.domain.Race;
import com.robotgame.dto.incoming.CityCreationDTO;
import com.robotgame.dto.outgoing.BuildingListDTO;
import com.robotgame.dto.outgoing.CityDetailsDTO;
import com.robotgame.dto.outgoing.RaceNameDTO;
import com.robotgame.repository.CityRepository;
import com.robotgame.repository.CustomUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CityService {

    private CityRepository cityRepository;
    private CustomUserRepository customUserRepository;

    public CityService(CityRepository cityRepository, CustomUserRepository customUserRepository) {
        this.cityRepository = cityRepository;
        this.customUserRepository = customUserRepository;
    }

    public void cityCreator(CityCreationDTO CCDTO) {
        City city = new City(CCDTO);

        Race race = switch (CCDTO.getRace()) {
            case "Exoquian" -> Race.EXOQUIAN;
            case "Human" -> Race.HUMAN;
            case "Nebulae" -> Race.NEBULAE;
            default -> null;
        };
        city.setRace(race);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);
        if (cityRepository.findByOwner(owner.getId()).isEmpty()) {
            city.setOwner(owner);
        }

        cityRepository.save(city);
    }

    public CityDetailsDTO cityDetailer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        return new CityDetailsDTO(city.getName(), city.getRace().getDisplayName(), city.getVault(), city.getArea(),
                                    city.getScore(), owner.getName());
    }

    public List<RaceNameDTO> raceLister() {
        return Arrays.stream(Race.values()).map(RaceNameDTO::new).toList();
    }

    public void builder(String building){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        city.getBuildings().computeIfPresent(Building.valueOf(building), (k, v) -> v + 1);
        city.getBuildings().putIfAbsent(Building.valueOf(building), 1L);
        city.setVault(city.getVault() - Building.valueOf(building).getCost());
    }

    public List<BuildingListDTO> buildingLister(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        List<BuildingListDTO> buildings = new ArrayList<>();
        for (Map.Entry<Building, Long> building : city.getBuildings().entrySet()) {
            buildings.add(new BuildingListDTO(building.getKey().getDisplayName(), building.getValue()));
        }
        return buildings;
    }
}
