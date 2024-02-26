package com.robotgame.controller;

import com.robotgame.dto.incoming.AuthenticationResponse;
import com.robotgame.dto.incoming.LoginRequestDTO;
import com.robotgame.dto.incoming.RegisterRequestDTO;
import com.robotgame.service.CustomUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class CustomUserController {
    private CustomUserService customUserService;

    public CustomUserController(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }


    @PostMapping("/reg")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequestDTO request){
        return new ResponseEntity<>(customUserService.register(request), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> auth(@RequestBody LoginRequestDTO request){
        return new ResponseEntity<>(customUserService.authenticate(request), HttpStatus.OK);
    }
    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> lout(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
