package com.divipay.auth.configuration.filter;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.divipay.auth.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;

public class JwtValidator extends OncePerRequestFilter{

	private JwtUtils jwtUtils;
	
	public JwtValidator(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if(jwt != null) {
			
			jwt = jwt.substring(7); // Deletes the "Bearer " of the token
			
			DecodedJWT decoded = jwtUtils.validateToken(jwt);
			String username = jwtUtils.getUsername(decoded);
			
			SecurityContext context = SecurityContextHolder.getContext();
			
			Authentication auth = new UsernamePasswordAuthenticationToken(username,
					null,new ArrayList<>());
		
			context.setAuthentication(auth);
			
			SecurityContextHolder.setContext(context);
			
		}
		
		filterChain.doFilter(request, response);
		
	}

}
