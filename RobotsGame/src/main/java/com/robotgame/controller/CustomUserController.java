package com.robotgame.controller;

import com.robotgame.dto.incoming.AuthenticationResponse;
import com.robotgame.dto.incoming.LoginRequestDTO;
import com.robotgame.dto.incoming.RegisterRequestDTO;
import com.robotgame.dto.outgoing.RoleSenderDTO;
import com.robotgame.dto.outgoing.UserListDTO;
import com.robotgame.service.CustomUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomUserController {
    private CustomUserService customUserService;

    public CustomUserController(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }


    @PostMapping("/users/reg")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequestDTO request) {
        return new ResponseEntity<>(customUserService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/users/login")
    public ResponseEntity<AuthenticationResponse> auth(@RequestBody LoginRequestDTO request) {
        return new ResponseEntity<>(customUserService.authenticate(request), HttpStatus.OK);
    }

    @PostMapping("/users/logout")
    public ResponseEntity<AuthenticationResponse> lout() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/userlist")
    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER')")
    public ResponseEntity<List<UserListDTO>> userLister() {
        return new ResponseEntity<>(customUserService.usersLister(), HttpStatus.OK);
    }

    @GetMapping("/userrole")
    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER')")
    public ResponseEntity<RoleSenderDTO> roleSender(){
        return new ResponseEntity<>(customUserService.roleSender(), HttpStatus.OK);
    }

}
