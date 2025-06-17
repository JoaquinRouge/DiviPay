package com.divipay.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.divipay.auth.dto.AuthLoginDto;
import com.divipay.auth.service.UserDetailsServiceImp;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserDetailsServiceImp userDetails;
	
	public AuthController(UserDetailsServiceImp userDetails) {
		this.userDetails = userDetails;
	}
	
	@PostMapping("/login")
	@PreAuthorize("permitAll()")
	public ResponseEntity<?> login(@RequestBody AuthLoginDto loginData){
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userDetails.login(loginData));
		}catch(UsernameNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}catch(BadCredentialsException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
}
