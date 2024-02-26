package com.robotgame.service;

import com.robotgame.config.JWTProcessor;
import com.robotgame.domain.CustomUser;
import com.robotgame.domain.UserRole;
import com.robotgame.dto.incoming.AuthenticationResponse;
import com.robotgame.dto.incoming.LoginRequestDTO;
import com.robotgame.dto.incoming.RegisterRequestDTO;
import com.robotgame.repository.CustomUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        cUser.setRole(UserRole.USER);
        cUser.setCreatedAt(LocalDateTime.now());
        if (customUserRepository.findByEmail(cUser.getEmail()).orElse(null) == null){
            customUserRepository.save(cUser);
            String jwt = processor.generateToken(cUser);
            return AuthenticationResponse.builder().token(jwt).build();
        } else {
            return null;
        }
    }

    public AuthenticationResponse authenticate(LoginRequestDTO request) {
        System.out.println(request);
        System.out.println(request.getEmail());
        System.out.println(request.getPassword());
        System.out.println("Mennyi?");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        System.out.println("30");
        CustomUser cUser = customUserRepository.findByMail(request.getEmail()).orElse(null);
        System.out.println(cUser);
        String jwt = processor.generateToken(cUser);
        System.out.println(jwt);

        return AuthenticationResponse.builder().token(jwt).build();
    }
}
