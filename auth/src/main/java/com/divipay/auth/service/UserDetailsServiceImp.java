package com.divipay.auth.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.divipay.auth.client.UserClient;
import com.divipay.auth.dto.AuthLoginDto;
import com.divipay.auth.dto.AuthResponseDto;
import com.divipay.auth.dto.UserModel;
import com.divipay.auth.utils.JwtUtils;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class UserDetailsServiceImp implements UserDetailsService{

	private JwtUtils jwtUtils;
	private UserClient userClient;
	private PasswordEncoder passwordEncoder;
	
	public UserDetailsServiceImp(JwtUtils jwtUtils,
			UserClient userClient,PasswordEncoder passwordEncoder) {
		this.jwtUtils = jwtUtils;
		this.passwordEncoder = passwordEncoder;
		this.userClient = userClient;
	}

	@Override
	@CircuitBreaker(name = "USER-SERVICE",fallbackMethod = "userFallbackMethod")
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		UserModel user = userClient.getUserByEmail(email);
		
		if(user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		
		return user;
	}
	
	public UserDetails userFallbackMethod(String email,Throwable t) {
		throw new UsernameNotFoundException("User service unavailable. Try again later.");
	}
	
	public AuthResponseDto login(AuthLoginDto data) {
		
		String username = data.email();
		String password = data.password();
		
		Authentication auth = authenticate(username,password);
		
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		String jwt = jwtUtils.generateToken(auth);
		
		return new AuthResponseDto(username,"login successful",jwt,true);
		
	}
	
	public Authentication authenticate(String username, String password) {
		
		UserDetails uDetails = loadUserByUsername(username);
		
		if(uDetails == null) {
			throw new BadCredentialsException("Error during authentication");
		}
		
		if(!passwordEncoder.matches(password, uDetails.getPassword())) {
			throw new BadCredentialsException("Error during authentication");
		}
		
		return new UsernamePasswordAuthenticationToken(uDetails, uDetails.getPassword(),
				uDetails.getAuthorities());
		
	}
	
}
