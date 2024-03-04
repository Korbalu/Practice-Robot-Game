package com.robotgame.service;

import com.robotgame.domain.City;
import com.robotgame.domain.CustomUser;
import com.robotgame.domain.Legion;
import com.robotgame.domain.Unit;
import com.robotgame.dto.outgoing.LegionListDTO;
import com.robotgame.dto.outgoing.UnitListDTO;
import com.robotgame.repository.ArmyRepository;
import com.robotgame.repository.CityRepository;
import com.robotgame.repository.CustomUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ArmyService {

    private ArmyRepository armyRepository;
    private CustomUserRepository customUserRepository;
    private CityRepository cityRepository;

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
        return Arrays.stream(Unit.values()).map(UnitListDTO::new).toList();
    }

    public void unitScorer(String thingToScore, City city, Long quantity) {
        city.setScore(city.getScore() + Unit.valueOf(thingToScore).getScore() * quantity);
        cityRepository.save(city);
    }

    public void battle(String enemyName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        List<Legion> ownArmy = armyRepository.findAllByOwner(owner.getId());

        List<Legion> enemyArmy = armyRepository.findAllByOwnerName(enemyName);
        long totalUnitCountEnemy = armyRepository.findUnitQuantity(enemyName);

        for (Legion legion : ownArmy) {
            for (Legion legion1 : enemyArmy) {
                Legion legionDB = armyRepository.findByOwnerAndType(owner.getId(), legion.getType());
                Legion legion2DB = armyRepository.findByOwnerNameAndType(enemyName, legion1.getType());

                boolean sameDefense = legion.getType().getAttackType().equals(legion1.getType().getArmorType());
                boolean sameDefense2 = legion1.getType().getAttackType().equals(legion.getType().getArmorType());

                long totalLegionAttack = legion.getQuantity() * legion.getType().getAttack();
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
                    if (sameDefense) {
                        singleStructureEnemy -= Math.max(legion.getType().getAttack() * 0.5 - legion1.getType().getArmor(), 1);
                    } else {
                        singleStructureEnemy -= Math.max(legion.getType().getAttack() - legion1.getType().getArmor(), 1);
                    }
                    if (singleStructureEnemy <= 0) {
                        singleStructureEnemy = legion1.getType().getStructure();
                        defendingLegion -= 1;
                    }
                    if (sameDefense2) {
                        singleStructureOwn -= legion1.getType().getAttack() * 0.5 - legion.getType().getArmor();
                    } else {
                        singleStructureOwn -= legion1.getType().getAttack() - legion.getType().getArmor();
                    }
                    if (singleStructureOwn <= 0) {
                        singleStructureOwn = legion.getType().getStructure();
                        attackingLegion -= 1;
                    }
                }

                legion1.setQuantity(defendingLegion > 0 ? defendingLegion : 0);
                legion.setQuantity(attackingLegion > 0 ? attackingLegion : 0);

                legionDB.setQuantity(legion.getQuantity());
                legion2DB.setQuantity(legion1.getQuantity());

                armyRepository.save(legionDB);
                armyRepository.save(legion2DB);
            }
        }
        armyRepository.deleteAllByQuantity(0L);
    }
}
