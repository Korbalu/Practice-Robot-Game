package com.robotgame.service;

import com.robotgame.domain.City;
import com.robotgame.domain.CustomUser;
import com.robotgame.domain.Legion;
import com.robotgame.domain.Unit;
import com.robotgame.repository.ArmyRepository;
import com.robotgame.repository.CityRepository;
import com.robotgame.repository.CustomUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MissionService {
    private CustomUserRepository customUserRepository;
    private CityRepository cityRepository;
    private ArmyRepository armyRepository;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public MissionService(CustomUserRepository customUserRepository, CityRepository cityRepository, ArmyRepository armyRepository) {
        this.customUserRepository = customUserRepository;
        this.cityRepository = cityRepository;
        this.armyRepository = armyRepository;
    }

    public void startExpedition(Long quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        City city = cityRepository.findByOwner(owner.getId()).orElse(null);

        Legion explorer = armyRepository.findByOwnerAndType(owner.getId(), Unit.LightBot);

        int expeditionTimeInMins = 2;
        if (explorer != null && explorer.getQuantity() >= quantity){
            explorer.setQuantity(explorer.getQuantity() - quantity);
            armyRepository.save(explorer);
            scheduler.schedule(() -> completeExpedition(city, quantity), expeditionTimeInMins, TimeUnit.MINUTES);
        }
    }

    public void completeExpedition (City city, Long quantity){
        city.setArea(city.getArea() + Math.round(quantity / 3));
        cityRepository.save(city);
    }
}
