package com.robotgame.service;

import com.robotgame.domain.*;
import com.robotgame.dto.outgoing.LegionNameQuantityListDTO;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MarketService {
    private ArmyRepository armyRepository;
    private CustomUserRepository customUserRepository;
    private CityRepository cityRepository;

    public MarketService(ArmyRepository armyRepository, CustomUserRepository customUserRepository, CityRepository cityRepository) {
        this.armyRepository = armyRepository;
        this.customUserRepository = customUserRepository;
        this.cityRepository = cityRepository;
    }

    public List<UnitListDTO> unitMarketLister() {
        return Arrays.stream(Unit.values()).filter(Unit::isBuyable).map(UnitListDTO::new).toList();
    }

    public void marketBuyUnit(String unit, Long quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        Legion legion = armyRepository.findByOwnerAndType(owner.getId(), Unit.valueOf(unit));

        long aggregateCost = Unit.valueOf(unit).getCost() * quantity;

        if (legion == null) {
            Legion legion2 = new Legion(Unit.valueOf(unit), quantity, city.getRace(), owner);
            if (city.getRace().getDisplayName().equals(legion2.getType().getRaceConnect()) ||
                    legion2.getType().getRaceConnect().equals("None")) {
                if (city.getVault() >= aggregateCost) {
                    city.setVault(city.getVault() - aggregateCost);
                    armyRepository.save(legion2);
                }
            } else {
                if (city.getVault() >= aggregateCost * 3) {
                    city.setVault(city.getVault() - aggregateCost * 3);
                    armyRepository.save(legion2);
                }
            }
        } else {
            if (city.getRace().getDisplayName().equals(legion.getType().getRaceConnect()) ||
                    legion.getType().getRaceConnect().equals("None")) {
                if (city.getVault() >= aggregateCost) {
                    city.setVault(city.getVault() - aggregateCost);
                    legion.setQuantity(legion.getQuantity() + quantity);
                    armyRepository.save(legion);
                }
            } else {
                if (city.getVault() >= aggregateCost * 3) {
                    city.setVault(city.getVault() - aggregateCost * 3);
                    legion.setQuantity(legion.getQuantity() + quantity);
                    armyRepository.save(legion);
                }
            }
        }
        scorer(city, owner);
    }

    public void marketSellUnit(String unit, Long quantity){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        Legion legion = armyRepository.findByOwnerAndType(owner.getId(), Unit.valueOf(unit));

        if (legion != null){
            legion.setQuantity(legion.getQuantity() - quantity);
            city.setVault(city.getVault() + legion.getType().getCost() * quantity);
            armyRepository.save(legion);
        }
        armyRepository.deleteAllByQuantity(0L);
        scorer(city, owner);
    }

    public List<LegionNameQuantityListDTO> legionsQuantityLister(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        return armyRepository.findAllByOwner(owner.getId()).stream().map(LegionNameQuantityListDTO::new).collect(Collectors.toList());
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
