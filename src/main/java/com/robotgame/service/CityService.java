package com.robotgame.service;

import com.robotgame.domain.*;
import com.robotgame.dto.incoming.CityCreationDTO;
import com.robotgame.dto.outgoing.*;
import com.robotgame.repository.ArmyRepository;
import com.robotgame.repository.CityRepository;
import com.robotgame.repository.CounterEntityRepository;
import com.robotgame.repository.CustomUserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CityService {

    private CityRepository cityRepository;
    private CustomUserRepository customUserRepository;
    private ArmyRepository armyRepository;
    private ArmyService armyService;

    private PasswordEncoder passwordEncoder;
    private CounterEntityRepository counterEntityRepository;

    public CityService(CityRepository cityRepository, CustomUserRepository customUserRepository, ArmyRepository armyRepository, ArmyService armyService, PasswordEncoder passwordEncoder, CounterEntityRepository counterEntityRepository) {
        this.cityRepository = cityRepository;
        this.customUserRepository = customUserRepository;
        this.armyRepository = armyRepository;
        this.armyService = armyService;
        this.passwordEncoder = passwordEncoder;
        this.counterEntityRepository = counterEntityRepository;
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
        long score = 0;
        for (Map.Entry<Building, Long> entry : city.getBuildings().entrySet()) {
            score += entry.getKey().getScore() * entry.getValue();
        }
        for (Legion legion : owner.getArmy()) {
            score += legion.getType().getScore() * legion.getQuantity();
        }
        city.setScore(score);

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
        scorer(city, owner);
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
                armyService.factoryIncrease(city, owner, Unit.LightBot.getDisplayName(), building.getKey().getProduction() * building.getValue());
            }
        }
        scorer(city,owner);
        cityRepository.save(city);
        owner.setTurns(owner.getTurns() - 1);
        customUserRepository.save(owner);
    }

    @Scheduled(cron = "0 17 0 * * ?")
    // it only works, if the app runs at 0:17!!! alternative: @Scheduled(fixedRate = 60000) // 1 minute interval
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

    public List<CityListDTO> cityLister() {
        return cityRepository.findAllOrderByScore().stream().map(CityListDTO::new).collect(Collectors.toList());
    }

    public LoggedInUserDetailsDTO userDetailer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        return new LoggedInUserDetailsDTO(owner.getId(), city.getId(), owner.getName(), city.getName(), city.getVault(), city.getScore());
    }

    public void buildingScorer(String thingToScore, City city) {
        city.setScore(city.getScore() + Building.valueOf(thingToScore.toUpperCase()).getScore());
    }

    public void autoUserCityCreator() {
        CounterEntity counterEntity = new CounterEntity();
        counterEntityRepository.save(counterEntity);

        CustomUser autoUser = new CustomUser();
        autoUser.setName("AutoUser" + counterEntity.getId());
        autoUser.setEmail("AutoEmail" + counterEntity.getId());
        autoUser.setPassword(passwordEncoder.encode("pass" + counterEntity.getId()));
        autoUser.setTurns(50);
        autoUser.setRole(UserRole.ROLE_USER);
        autoUser.setCreatedAt(LocalDateTime.now());
        autoUser.setLastTimeTurnGiven(LocalDateTime.now());
        autoUser.setArmy(new ArrayList<>());
        customUserRepository.save(autoUser);

        Random random = new Random();
        Race[] races = Race.values();
        int raceIndex = random.nextInt(races.length);
        String randomRace = races[raceIndex].getDisplayName();
        Race race = switch (randomRace) {
            case "Exoquian" -> Race.EXOQUIAN;
            case "Human" -> Race.HUMAN;
            case "Nebulae" -> Race.NEBULAE;
            default -> null;
        };

        City city = new City("AutoCity" + counterEntity.getId(), race, autoUser);
        cityRepository.save(city);

        Building[] buildings = Building.values();
        Unit[] units = Unit.values();

        while (city.getVault() > 100) {
            int decider = random.nextInt(100);
            if (decider < 20) {
                int buildingIndex = random.nextInt(buildings.length);
                String randomBuilding = buildings[buildingIndex].getDisplayName();
                city.getBuildings().computeIfPresent(Building.valueOf(randomBuilding.toUpperCase()), (k, v) -> v + 1);
                city.getBuildings().putIfAbsent(Building.valueOf(randomBuilding.toUpperCase()), 1L);
                city.setVault(city.getVault() - Building.valueOf(randomBuilding.toUpperCase()).getCost());
                city.setArea(city.getArea() - 1);
            } else {
                int unitIndex = random.nextInt(units.length);
                String randomUnit = units[unitIndex].getDisplayName();
                Legion legion = armyRepository.findByOwnerAndType(autoUser.getId(), Unit.valueOf(randomUnit));
                if (legion == null) {
                    Legion legion2 = new Legion(Unit.valueOf(randomUnit), 1L, city.getRace(), autoUser);
                    city.setVault(city.getVault() - Unit.valueOf(randomUnit).getCost());
                    armyRepository.save(legion2);
                } else {
                    city.setVault(city.getVault() - Unit.valueOf(randomUnit).getCost());
                    legion.setQuantity(legion.getQuantity() + 1L);
                    armyRepository.save(legion);
                }
            }
            cityRepository.save(city);
        }
        scorer(city, autoUser);
    }

    public void scorer(City city, CustomUser owner) {
        long finalScore = 0;
        for (Map.Entry<Building, Long> building : city.getBuildings().entrySet()) {
            finalScore += building.getValue() * building.getKey().getScore();
        }
        for (Legion legion : owner.getArmy()) {
            finalScore += legion.getType().getScore() * legion.getQuantity();
        }
        city.setScore(finalScore);
        cityRepository.save(city);
    }
}
