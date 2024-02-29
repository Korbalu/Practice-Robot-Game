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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        if (legion == null) {
            Legion legion1 = new Legion(Unit.valueOf(unit), quantity, city.getRace(), owner);
            city.setVault(city.getVault() - Unit.valueOf(unit).getCost());
            armyRepository.save(legion1);
        } else {
            city.setVault(city.getVault() - Unit.valueOf(unit).getCost());
            legion.setQuantity(legion.getQuantity() + quantity);
        }
    }

    public List<LegionListDTO> armyLister(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        return armyRepository.findAllByOwner(owner.getId()).stream().map(LegionListDTO::new).toList();
    }

    public List<UnitListDTO> unitLister(){
        return Arrays.stream(Unit.values()).map(UnitListDTO::new).toList();
    }
}
