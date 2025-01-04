package com.example.demo.TinyURL.Controller;

import com.example.demo.TinyURL.DTO.LoginRequest;
import com.example.demo.TinyURL.DTO.LoginResponse;
import com.example.demo.TinyURL.DTO.RegisterRequest;
import com.example.demo.TinyURL.Entity.User;
import com.example.demo.TinyURL.JwtService.JwtService;
import com.example.demo.TinyURL.Service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.remote.JMXAuthenticator;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try{
            authenticationService.register(registerRequest);
            // Authenticate the user after registration

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Generate a JWT token for the newly registered user
            String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            // For frontend to fetch username
            response.put("username", registerRequest.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User authenticatedUser = authenticationService.login(loginRequest);
            String jwtToken = jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());
            loginResponse.setMessage(loginRequest.getUsername() + " Login Successful");
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        }

    }
}
