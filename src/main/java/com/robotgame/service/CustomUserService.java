package com.robotgame.service;

import com.robotgame.config.JWTProcessor;
import com.robotgame.domain.CustomUser;
import com.robotgame.domain.UserRole;
import com.robotgame.dto.incoming.AuthenticationResponse;
import com.robotgame.dto.incoming.LoginRequestDTO;
import com.robotgame.dto.incoming.RegisterRequestDTO;
import com.robotgame.dto.outgoing.RoleSenderDTO;
import com.robotgame.dto.outgoing.UserListDTO;
import com.robotgame.repository.CustomUserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserService {

    private CustomUserRepository customUserRepository;
    private PasswordEncoder passwordEncoder;
    private JWTProcessor processor;
    private AuthenticationManager authenticationManager;

    public CustomUserService(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder,
                             JWTProcessor processor, AuthenticationManager authenticationManager) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.processor = processor;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequestDTO request) {
        CustomUser cUser = new CustomUser();
        cUser.setName(request.getName());
        cUser.setEmail(request.getEmail());
        cUser.setPassword(passwordEncoder.encode(request.getPassword()));
        cUser.setTurns(50);
        cUser.setResourcesSent(0L);
        cUser.setRole(UserRole.ROLE_USER);
        cUser.setCreatedAt(LocalDateTime.now());
        cUser.setLastTimeTurnGiven(cUser.getCreatedAt());
        if (customUserRepository.findByEmail(cUser.getEmail()).orElse(null) == null) {
            customUserRepository.save(cUser);
            String jwt = processor.generateToken(cUser);
            return AuthenticationResponse.builder().token(jwt).build();
        } else {
            return null;
        }
    }

    public AuthenticationResponse authenticate(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        CustomUser cUser = customUserRepository.findByMail(request.getEmail()).orElse(null);
        String jwt = processor.generateToken(cUser);
        return AuthenticationResponse.builder().token(jwt).build();
    }

    public List<UserListDTO> usersLister() {
        return customUserRepository.findAll().stream().map(UserListDTO::new).collect(Collectors.toList());
    }

    public RoleSenderDTO roleSender() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
        CustomUser owner = customUserRepository.findByMail(loggedInUser.getUsername()).orElse(null);
        RoleSenderDTO roleSenderDTO = new RoleSenderDTO();
        roleSenderDTO.setRole(owner.getRole().getDisplayName());
        return roleSenderDTO;
    }

}
