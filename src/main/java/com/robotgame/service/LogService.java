package com.robotgame.service;

import com.robotgame.domain.CustomUser;
import com.robotgame.dto.outgoing.LogListDTO;
import com.robotgame.repository.CustomUserRepository;
import com.robotgame.repository.LogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {
    private LogRepository logRepository;
    private CustomUserRepository customUserRepository;

    public LogService(LogRepository logRepository, CustomUserRepository customUserRepository) {
        this.logRepository = logRepository;
        this.customUserRepository = customUserRepository;
    }

    public List<LogListDTO> logLister(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);

        return logRepository.findAllById(owner.getId()).stream().map(LogListDTO::new).toList();
    }
}
