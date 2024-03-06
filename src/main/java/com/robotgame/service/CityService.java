package com.robotgame.service;

import com.robotgame.domain.*;
import com.robotgame.dto.incoming.CityCreationDTO;
import com.robotgame.dto.outgoing.*;
import com.robotgame.repository.CityRepository;
import com.robotgame.repository.CustomUserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CityService {

    private CityRepository cityRepository;
    private CustomUserRepository customUserRepository;
    private ArmyService armyService;

    public CityService(CityRepository cityRepository, CustomUserRepository customUserRepository, ArmyService armyService) {
        this.cityRepository = cityRepository;
        this.customUserRepository = customUserRepository;
        this.armyService = armyService;
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
                city.getScore(), owner.getTurns(), owner.getName());
    }

    public List<RaceNameDTO> raceLister() {
        return Arrays.stream(Race.values()).map(RaceNameDTO::new).toList();
    }

    public void builder(String building) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        if (city.getVault() >= Building.valueOf(building.toUpperCase()).getCost() && city.getArea() > 0) {
            city.getBuildings().computeIfPresent(Building.valueOf(building.toUpperCase()), (k, v) -> v + 1);
            city.getBuildings().putIfAbsent(Building.valueOf(building.toUpperCase()), 1L);
            city.setVault(city.getVault() - Building.valueOf(building.toUpperCase()).getCost());
            city.setArea(city.getArea() - 1);
            buildingScorer(building, city);
        }
        cityRepository.save(city);
    }

    public List<BuildingListDTO> buildingLister() {
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

    public List<AllBuildingsListDTO> everyBuildingsLister() {
        return Arrays.stream(Building.values()).map(AllBuildingsListDTO::new).toList();
    }

    public void newTurn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        for (Map.Entry<Building, Long> building : city.getBuildings().entrySet()) {
            if (building.getKey().equals(Building.CRYSTALMINE)) {
                city.setVault(city.getVault() + building.getKey().getProduction() * building.getValue());
            }
            if (building.getKey().equals(Building.FACTORY)) {
                armyService.increaseUnit(Unit.LightBot.getDisplayName(), (long) building.getKey().getProduction() * building.getValue());
                city.setVault(city.getVault() + Unit.LightBot.getCost() * building.getKey().getProduction() * building.getValue());
            }
        }
        cityRepository.save(city);
        owner.setTurns(owner.getTurns() - 1);
        customUserRepository.save(owner);
    }

    @Scheduled(cron = "0 17 0 * * ?") // it only works, if the app runs at 0:17!!!
    public void vaultDecreaser() {
        List<City> cities = cityRepository.findAll();

        Random random = new Random();
        int randomTaxKey = 1 + random.nextInt(5);
        for (City city : cities) {
            long tax = city.getVault() * randomTaxKey / 100;
            city.setVault(city.getVault() - tax);
            cityRepository.save(city);
        }
        System.out.println("taxing done");
    }

    public List<CityListDTO> cityLister(){
        return cityRepository.findAllOrderByScore().stream().map(CityListDTO::new).collect(Collectors.toList());
    }

    public LoggedInUserDetailsDTO userDetailer(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        return new LoggedInUserDetailsDTO(owner.getId(), city.getId(), owner.getName(), city.getName(), city.getVault(), city.getScore());
    }

    public void buildingScorer(String thingToScore, City city) {
        city.setScore(city.getScore() + Building.valueOf(thingToScore.toUpperCase()).getScore());
    }
}
