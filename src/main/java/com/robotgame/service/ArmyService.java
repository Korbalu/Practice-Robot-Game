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
}
