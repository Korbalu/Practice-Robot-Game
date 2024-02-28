package com.robotgame.service;

import com.robotgame.domain.City;
import com.robotgame.domain.CustomUser;
import com.robotgame.domain.Race;
import com.robotgame.dto.incoming.CityCreationDTO;
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
import java.util.stream.Collectors;

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
        if (cityRepository.findByOwner(owner.getId()).isEmpty()){
            city.setOwner(owner);
        }

        cityRepository.save(city);
    }

    public List<RaceNameDTO> raceLister(){
        return Arrays.stream(Race.values()).map(RaceNameDTO::new).toList();
    }
}
