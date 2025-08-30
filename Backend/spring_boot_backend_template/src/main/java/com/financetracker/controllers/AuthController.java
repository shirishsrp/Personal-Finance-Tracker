package com.financetracker.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.financetracker.dto.AuthRequest;
import com.financetracker.dto.AuthResponse;
import com.financetracker.dto.UserReqDTO;
import com.financetracker.services.UserService;
import com.financetracker.security.JwtUtils;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserReqDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(dto));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody @Valid AuthRequest dto) {
        try {
            UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
            Authentication auth = authenticationManager.authenticate(token);
            String jwt = jwtUtils.generateJwtToken(auth);
            return ResponseEntity.ok(new AuthResponse("Authentication successful", jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Authentication failed: " + e.getMessage(), null));
        }
    }
}
