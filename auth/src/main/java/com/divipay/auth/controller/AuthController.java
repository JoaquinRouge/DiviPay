package com.divipay.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.divipay.auth.dto.AuthLoginDto;
import com.divipay.auth.service.UserDetailsServiceImp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller", description = "Handles user login and authentication")
public class AuthController {

    private final UserDetailsServiceImp userDetails;

    public AuthController(UserDetailsServiceImp userDetails) {
        this.userDetails = userDetails;
    }

    @Operation(
        summary = "Authenticate a user",
        description = "Authenticates a user using email and password, and returns a JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "400", description = "Invalid credentials or user not found")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Parameter(description = "Login data (email and password)", required = true)
        @RequestBody AuthLoginDto loginData
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userDetails.login(loginData));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
