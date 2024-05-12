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
        int[] coordinates = cityCoordCreater();
        city.setX((long) coordinates[0]);
        city.setY((long) coordinates[1]);

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
                city.getFreeArea(), city.getScore(), owner.getTurns(), owner.getName(), city.getX(), city.getY());
    }

    public List<RaceNameDTO> raceLister() {
        return Arrays.stream(Race.values()).map(RaceNameDTO::new).toList();
    }

    public void builder(String building) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        if (city.getVault() >= Building.valueOf(building.toUpperCase()).getCost() && city.getFreeArea() > 0) {
            city.getBuildings().computeIfPresent(Building.valueOf(building.toUpperCase()), (k, v) -> v + 1);
            city.getBuildings().putIfAbsent(Building.valueOf(building.toUpperCase()), 1L);
            city.setVault(city.getVault() - Building.valueOf(building.toUpperCase()).getCost());
            city.setFreeArea(city.getFreeArea() - 1);
            buildingScorer(building, city);
        }
        armyService.scorer(city, owner);
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

        Unit specialUnit = Arrays.stream(Unit.values()).
                filter(unit -> unit.getRaceConnect().equals(city.getRace().getDisplayName()))
                .toArray(Unit[]::new)[0];

        for (Map.Entry<Building, Long> building : city.getBuildings().entrySet()) {
            if (building.getKey().equals(Building.CRYSTALMINE)) {
                city.setVault(city.getVault() + building.getKey().getProduction() * building.getValue());
            }
            if (building.getKey().equals(Building.MANUFACTUREPLANT)) {
                city.setVault(city.getVault() + building.getKey().getProduction() * building.getValue());
                armyService.factoryIncrease(city, owner, Unit.LightBot.getDisplayName(), building.getKey().getProduction() * building.getValue());
            }
            if (building.getKey().equals(Building.FACTORY)) {
                armyService.factoryIncrease(city, owner, Unit.LightBot.getDisplayName(), building.getKey().getProduction() * building.getValue());
            }
            if (building.getKey().equals(Building.SPECIALIZER)) {
                armyService.factoryIncrease(city, owner, specialUnit.getDisplayName(), building.getKey().getProduction() * building.getValue());
            }
        }
        armyService.scorer(city, owner);
        cityRepository.save(city);
        owner.setTurns(owner.getTurns() - 1);
        customUserRepository.save(owner);
    }

    @Scheduled(cron = "0 17 0 * * ?")
    // it only works, if the app runs at 0:17!!! alternative: @Scheduled(fixedRate = 60000) // 1 minute interval
    public void vaultDecreaser() {
        List<City> cities = cityRepository.findAll();

        Random random = new Random();
        int randomTaxKey = 1 + random.nextInt(8);
        for (City city : cities) {
            long tax = city.getVault() * randomTaxKey / 100;
            city.setVault(city.getVault() - tax);
            cityRepository.save(city);
        }
        System.out.println("taxing done");

        List<Legion> allLightBotLegions = armyRepository.findALLByType(Unit.LightBot);
        for (Legion lightBotLegion : allLightBotLegions) {
            lightBotLegion.setQuantity((long) (lightBotLegion.getQuantity() * 0.8));
            Legion legion2 = new Legion(Unit.LightBotUpg, (long) (lightBotLegion.getQuantity() * (1 - 0.8)), lightBotLegion.getRace(), lightBotLegion.getOwner());
            armyRepository.save(lightBotLegion);
            armyRepository.save(legion2);
            System.out.println("armies changed");
        }
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
        int[] coordinates = cityCoordCreater();
        city.setX((long) coordinates[0]);
        city.setY((long) coordinates[1]);
        cityRepository.save(city);

        Building[] buildings = Building.values();
        Unit[] units = Arrays.stream(Unit.values()).
                filter(unit -> unit != Unit.LightBotUpg &&
                        (unit.getRaceConnect().equals(race.getDisplayName()) || unit.getRaceConnect().equals("None")))
                .toArray(Unit[]::new);

        while (city.getVault() > 100) {
            int decider = random.nextInt(100);
            if (decider < 20 && city.getFreeArea() > 0) {
                int buildingIndex = random.nextInt(buildings.length);
                String randomBuilding = buildings[buildingIndex].getDisplayName();
                city.getBuildings().computeIfPresent(Building.valueOf(randomBuilding.toUpperCase()), (k, v) -> v + 1);
                city.getBuildings().putIfAbsent(Building.valueOf(randomBuilding.toUpperCase()), 1L);
                city.setVault(city.getVault() - Building.valueOf(randomBuilding.toUpperCase()).getCost());
                city.setFreeArea(city.getFreeArea() - 1);
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
        armyService.scorer(city, autoUser);
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

    public int[] cityCoordCreater() {
        Random random = new Random();
        int[] coordinates = new int[2];

        List<City> cities = cityRepository.findAllOrderByScore();

        boolean decider;

        do {
            decider = false;
            coordinates[0] = random.nextInt(21) + 1;
            coordinates[1] = random.nextInt(21) + 1;
            for (City oneCity : cities) {
                if (oneCity.getX() == coordinates[0] && oneCity.getY() == coordinates[1]) {
                    decider = true;
                    break;
                }
            }
        } while (decider);

        return coordinates;
    }

    public void newTurn4Bots() {
        List<City> cities = cityRepository.findAllAutoCity();

        for (City city : cities) {
            while (city.getOwner().getTurns() > 0) {
                manufacturePriorityForBots(city);
                for (Map.Entry<Building, Long> building : city.getBuildings().entrySet()) {
                    if (building.getKey().equals(Building.CRYSTALMINE)) {
                        city.setVault(city.getVault() + building.getKey().getProduction() * building.getValue());
                    }
                    if (building.getKey().equals(Building.FACTORY)) {
                        armyService.factoryIncrease(city, city.getOwner(), Unit.LightBot.getDisplayName(),
                                building.getKey().getProduction() * building.getValue());
                    }
                }
                city.getOwner().setTurns(city.getOwner().getTurns() - 1);
                cityRepository.save(city);
                customUserRepository.save(city.getOwner());
            }
            scorer(city, city.getOwner());
        }
    }

    private void manufacturePriorityForBots(City city) {
        CustomUser owner = city.getOwner();
        if (city.getFreeArea() > 0 && city.getVault() > 100) {
            if (owner.getTurns() > 20) {
                for (Building building : Building.values()) {
                    if ((building.equals(Building.CRYSTALMINE) || building.equals(Building.FACTORY))
                            && (!city.getBuildings().containsKey(building) || city.getBuildings().get(building) < 7)) {
                        city.getBuildings().computeIfPresent(building, (k, v) -> v + 1);
                        city.getBuildings().computeIfAbsent(building, k -> 1L);
                        city.setVault(city.getVault() - building.getCost());
                        city.setFreeArea(city.getFreeArea() - 1);
                        buildingScorer(building.getDisplayName(), city);
                    }
                }
            }
        }
        boolean hasLightBot = owner.getArmy().stream().anyMatch(legion -> legion.getType() == Unit.LightBot);
        if (city.getFreeArea() == 0) {
            Legion legion = armyRepository.findByOwnerAndType(owner.getId(), Unit.LightBot);
            if (hasLightBot) {
                city.setArea(city.getArea() + 1);
                city.setFreeArea(city.getFreeArea() + 1);
                legion.setQuantity(legion.getQuantity() - 1);
                armyRepository.save(legion);
                armyRepository.deleteAllByQuantity(0L);
            } else {
                armyService.unitAdder(city, owner, Unit.LightBot, legion);
            }
        }
        if (city.getVault() > 200) {
            armyService.randomUnitIncreaser(city, owner);
        }
        cityRepository.save(city);
    }
}
