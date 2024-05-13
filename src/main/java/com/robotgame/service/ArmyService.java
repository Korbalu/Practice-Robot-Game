package com.robotgame.service;

import com.robotgame.domain.*;
import com.robotgame.dto.outgoing.LegionListDTO;
import com.robotgame.dto.outgoing.UnitListDTO;
import com.robotgame.repository.ArmyRepository;
import com.robotgame.repository.CityRepository;
import com.robotgame.repository.CustomUserRepository;
import com.robotgame.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ArmyService {

    private ArmyRepository armyRepository;
    private CustomUserRepository customUserRepository;
    private CityRepository cityRepository;
    private LogRepository logRepository;
    private static final Logger logger = LoggerFactory.getLogger(ArmyService.class);

    public ArmyService(ArmyRepository armyRepository, CustomUserRepository customUserRepository, CityRepository cityRepository, LogRepository logRepository) {
        this.armyRepository = armyRepository;
        this.customUserRepository = customUserRepository;
        this.cityRepository = cityRepository;
        this.logRepository = logRepository;
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

        List<Legion> ownArmy = armyRepository.findAllByOwner(owner.getId()); //Sort from Repository side based on attack type, if it can be done, I couldn't
        ownArmy.sort(Comparator.comparing(legion -> legion.getType().getAttackType()));
        List<Legion> enemyArmy = armyRepository.findAllByOwnerName(enemyName);

        long totalUnitCountEnemy = armyRepository.findUnitQuantity(enemyName) == null ? 0 : armyRepository.findUnitQuantity(enemyName);

        long ownScoreLoss = 0;
        long enemyScoreLoss = 0;

        StringBuilder logBodyAttacker = new StringBuilder();
        StringBuilder logBodyDefender = new StringBuilder();
        logBodyAttacker.append("Attacker: " + owner.getName() + " Defender: " + enemyName);
        logBodyDefender.append("Attacker: " + owner.getName() + " Defender: " + enemyName);


        for (Legion legion : ownArmy) {
            for (Legion legion1 : enemyArmy) {
                Legion legionDB = armyRepository.findByOwnerAndType(owner.getId(), legion.getType());
                Legion legion2DB = armyRepository.findByOwnerNameAndType(enemyName, legion1.getType());

                boolean sameDefense = legion.getType().getAttackType().equals(legion1.getType().getArmorType());
                boolean sameDefense2 = legion1.getType().getAttackType().equals(legion.getType().getArmorType());

                long totalLegionAttack = legion.getQuantity() * legion.getType().getAttack();
                logBodyAttacker.append("\n Attack Strength: " + totalLegionAttack);
                long partialLegionAttack;

                if (sameDefense) {
                    partialLegionAttack = Math.round(totalLegionAttack * (legion1.getQuantity() / totalUnitCountEnemy) * 0.5);
                } else {
                    partialLegionAttack = Math.round(totalLegionAttack * (legion1.getQuantity() / totalUnitCountEnemy));
                }

                int singleStructureEnemy = legion1.getType().getStructure();
                long defendingLegion = legion1.getQuantity();
                int singleStructureOwn = legion.getType().getStructure();
                long attackingLegion = legion.getQuantity();

                while (partialLegionAttack >= 0 && defendingLegion > 0) {
                    partialLegionAttack -= legion.getType().getAttack();
                    int defendersArmor = legion1.getType().getArmor();
                    if (enemyCity.getBuildings().containsKey(Building.WALL)) {
                        defendersArmor *= 1.01 * enemyCity.getBuildings().get(Building.WALL);
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
                        logBodyDefender.append("\n Unit lost: " + legion1.getType().getDisplayName());
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
                        logBodyAttacker.append("\n Unit lost: " + legion.getType().getDisplayName());
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
                logBodyAttacker.append("\n Victory! Area taken: " + areaGain);
                logBodyDefender.append("\n Defeat! Area taken: " + areaGain);
            } else if (attackType.equals("raid")) {
                long wealthGain = Math.round(enemyCity.getVault() * 0.1);
                enemyCity.setVault(enemyCity.getVault() - wealthGain);
                ownCity.setVault(ownCity.getVault() + wealthGain);
                logBodyAttacker.append("\n Victory! Vault increase: " + wealthGain);
                logBodyDefender.append("\n Defeat! Vault increase: " + wealthGain);
            }
        } else {
            logBodyAttacker.append("\n Defeat!");
            logBodyDefender.append("\n Victory!");
        }
        Log attackerLog = new Log("Battle", new String(logBodyAttacker), owner);
        Log defenderLog = new Log("Battle", new String(logBodyDefender), enemyCity.getOwner());
        logRepository.save(attackerLog);
        logRepository.save(defenderLog);

        armyRepository.deleteAllByQuantity(0L);
        scorer(ownCity, owner);
        scorer(enemyCity, enemyCity.getOwner());
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
}
