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

@Service
public class LogService {
    private LogRepository logRepository;
    private CustomUserService customUserService;

    public LogService(LogRepository logRepository, CustomUserService customUserService) {
        this.logRepository = logRepository;
        this.customUserService = customUserService;
    }

    public List<LogListDTO> logLister(){
        CustomUser owner = customUserService.loggedInUserFinder();

        return logRepository.findAllById(owner.getId()).stream().map(LogListDTO::new).toList();
    }
}
