package com.robotgame.service;

import com.robotgame.domain.*;
import com.robotgame.dto.outgoing.LegionListDTO;
import com.robotgame.dto.outgoing.UnitListDTO;
import com.robotgame.repository.ArmyRepository;
import com.robotgame.repository.CityRepository;
import com.robotgame.repository.CustomUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArmyService {

    private ArmyRepository armyRepository;
    private CustomUserRepository customUserRepository;
    private CityRepository cityRepository;
    private static final Logger logger = LoggerFactory.getLogger(ArmyService.class);

    public ArmyService(ArmyRepository armyRepository, CustomUserRepository customUserRepository, CityRepository cityRepository) {
        this.armyRepository = armyRepository;
        this.customUserRepository = customUserRepository;
        this.cityRepository = cityRepository;
    }

    public void increaseUnit(String unit, Long quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        Legion legion = armyRepository.findByOwnerAndType(owner.getId(), Unit.valueOf(unit));

        if (city.getVault() >= Unit.valueOf(unit).getCost() * quantity) {
            if (legion == null) {
                Legion legion2 = new Legion(Unit.valueOf(unit), quantity, city.getRace(), owner);
                city.setVault(city.getVault() - Unit.valueOf(unit).getCost() * quantity);
                armyRepository.save(legion2);
            } else {
                city.setVault(city.getVault() - Unit.valueOf(unit).getCost() * quantity);
                legion.setQuantity(legion.getQuantity() + quantity);
                armyRepository.save(legion);
            }
        }
        unitScorer(unit, city, quantity);
    }

    public List<LegionListDTO> armyLister() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        return armyRepository.findAllByOwner(owner.getId()).stream().map(LegionListDTO::new).toList();
    }

    public List<UnitListDTO> unitLister() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        return Arrays.stream(Unit.values())
                .filter(Unit::isBuyable)
                .filter(unit -> unit.getRaceConnect().equals(city.getRace().getDisplayName()) || unit.getRaceConnect().equals("None"))
                .map(UnitListDTO::new)
                .toList();
    }

    public void unitScorer(String thingToScore, City city, Long quantity) {
        city.setScore(city.getScore() + Unit.valueOf(thingToScore).getScore() * quantity);
        cityRepository.save(city);
    }

    public void battle(String enemyName, String attackType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City ownCity = cityRepository.findByOwner(owner.getId()).orElse(null);
        City enemyCity = cityRepository.findByOwnerName(enemyName).orElse(null);

        List<Legion> ownArmy = armyRepository.findAllByOwner(owner.getId());//Sort from Repository side based on attack type, if it can be done... I couldn't
        ownArmy.sort(Comparator.comparing(legion -> legion.getType().getAttackType()));
        List<Legion> enemyArmy = armyRepository.findAllByOwnerName(enemyName);

        long totalUnitCountEnemy = armyRepository.findUnitQuantity(enemyName) == null ? 0 : armyRepository.findUnitQuantity(enemyName);

        long ownScoreLoss = 0;
        long enemyScoreLoss = 0;

        for (Legion legion : ownArmy) {
            for (Legion legion1 : enemyArmy) {
                Legion legionDB = armyRepository.findByOwnerAndType(owner.getId(), legion.getType());
                Legion legion2DB = armyRepository.findByOwnerNameAndType(enemyName, legion1.getType());

                boolean sameDefense = legion.getType().getAttackType().equals(legion1.getType().getArmorType());
                boolean sameDefense2 = legion1.getType().getAttackType().equals(legion.getType().getArmorType());

                long totalLegionAttack = legion.getQuantity() * legion.getType().getAttack();
                long partialLegionAttack;

                if (sameDefense) {
                    partialLegionAttack = Math.round(totalLegionAttack * ((double) legion1.getQuantity() / totalUnitCountEnemy) * 0.5);
                } else {
                    partialLegionAttack = Math.round(totalLegionAttack * ((double) legion1.getQuantity() / totalUnitCountEnemy));
                }

                int singleStructureEnemy = legion1.getType().getStructure();
                long defendingLegion = legion1.getQuantity();
                int singleStructureOwn = legion.getType().getStructure();
                long attackingLegion = legion.getQuantity();

                while (partialLegionAttack >= 0 && defendingLegion > 0) {
                    partialLegionAttack -= legion.getType().getAttack();
                    int defendersArmor = legion1.getType().getArmor();
                    if (enemyCity.getBuildings().containsKey(Building.WALL)) {
                        defendersArmor *= Math.min(1 + (Building.WALL.getProduction() / 100.0) * enemyCity.getBuildings().get(Building.WALL), 1.3);
                    }
                    if (enemyCity.getBuildings().containsKey(Building.LIGHTREPELLER) && legion.getType().equals(Unit.LightBot)) {
                        defendersArmor *= Math.min(1 + (Building.LIGHTREPELLER.getProduction() / 100.0) * enemyCity.getBuildings().get(Building.LIGHTREPELLER), 1.2);
                    }
                    if (sameDefense) {
                        singleStructureEnemy -= Math.max(legion.getType().getAttack() * 0.5 - defendersArmor, 1);
                    } else {
                        singleStructureEnemy -= Math.max(legion.getType().getAttack() - defendersArmor, 1);
                    }
                    if (singleStructureEnemy <= 0) {
                        singleStructureEnemy = legion1.getType().getStructure();
                        defendingLegion -= 1;
                        enemyScoreLoss += legion2DB.getType().getScore();
                    }
                    if (sameDefense2) {
                        singleStructureOwn -= legion1.getType().getAttack() * 0.5 - legion.getType().getArmor();
                    } else {
                        singleStructureOwn -= legion1.getType().getAttack() - legion.getType().getArmor();
                    }
                    if (singleStructureOwn <= 0) {
                        singleStructureOwn = legion.getType().getStructure();
                        attackingLegion -= 1;
                        ownScoreLoss += legionDB.getType().getScore();
                    }
                }

                legion1.setQuantity(defendingLegion > 0 ? defendingLegion : 0);
                legion.setQuantity(attackingLegion > 0 ? attackingLegion : 0);

                legionDB.setQuantity(legion.getQuantity());
                legion2DB.setQuantity(legion1.getQuantity());
            }
        }

        if (ownScoreLoss < enemyScoreLoss || ownScoreLoss == 0) {
            logger.info("Battle started between {} and {}", ownCity.getName(), enemyCity.getName());
            if (attackType.equals("conquer")) {
                long areaGain = Math.max(Math.round(enemyCity.getArea() * 0.05), 1);
                enemyCity.setArea(enemyCity.getArea() - areaGain);
                ownCity.setArea(ownCity.getArea() + areaGain);
            } else if (attackType.equals("raid")) {
                long wealthGain = Math.round(enemyCity.getVault() * 0.1);
                enemyCity.setVault(enemyCity.getVault() - wealthGain);
                ownCity.setVault(ownCity.getVault() + wealthGain);
            }
        }
        armyRepository.deleteAllByQuantity(0L);
        scorer(ownCity, owner);
    }

    public void factoryIncrease(City city, CustomUser owner, String unit, Long quantity) {
        Legion legion = armyRepository.findByOwnerAndType(owner.getId(), Unit.valueOf(unit));

        if (legion == null) {
            Legion legion2 = new Legion(Unit.valueOf(unit), quantity, city.getRace(), owner);
            armyRepository.save(legion2);
        } else {
            legion.setQuantity(legion.getQuantity() + quantity);
            armyRepository.save(legion);
        }
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

    public void randomUnitIncreaser(City city, CustomUser owner) {
        Race race = city.getRace();
        Unit specialUnit = Unit.LightBot;
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1;
        for (Unit unit : Unit.values()) {
            if (unit.getRaceConnect().equals(race.getDisplayName())) {
                specialUnit = unit;
            }
        }

        if (randomNumber < 61) {
            Legion legion = armyRepository.findByOwnerAndType(owner.getId(), specialUnit);
            if (city.getVault() > specialUnit.getCost()) {
                unitAdder(city, owner, specialUnit, legion);
            }
        } else if (randomNumber < 81) {
            Legion legion = armyRepository.findByOwnerAndType(owner.getId(), Unit.LightBot);
            if (city.getVault() > Unit.LightBot.getCost()) {
                unitAdder(city, owner, Unit.LightBot, legion);
            }
        } else if (randomNumber < 91) {
            Legion legion = armyRepository.findByOwnerAndType(owner.getId(), Unit.Bomber);
            if (city.getVault() > Unit.Bomber.getCost()) {
                unitAdder(city, owner, Unit.Bomber, legion);
            }
        } else {
            Legion legion = armyRepository.findByOwnerAndType(owner.getId(), Unit.HeavyHitter);
            if (city.getVault() > Unit.HeavyHitter.getCost()) {
                unitAdder(city, owner, Unit.HeavyHitter, legion);
            }
        }
    }

    public void unitAdder(City city, CustomUser owner, Unit specialUnit, Legion legion) {
        if (city.getVault() >= specialUnit.getCost()) {
            if (legion == null) {
                Legion legion2 = new Legion(specialUnit, 1L, city.getRace(), owner);
                city.setVault(city.getVault() - specialUnit.getCost());
                armyRepository.save(legion2);
            } else {
                city.setVault(city.getVault() - specialUnit.getCost());
                legion.setQuantity(legion.getQuantity() + 1L);
                armyRepository.save(legion);
            }
        }
    }
}
