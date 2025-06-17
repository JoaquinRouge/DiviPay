package com.divipay.auth.utils;


import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.divipay.auth.dto.UserModel;

@Component
public class JwtUtils {

	@Value("${security.jwt.private.key}")
	private String key;
	
	@Value("${security.jwt.user.generator}")
	private String userGenerator;
	
	public String generateToken(Authentication auth) {
			
			Algorithm alg = Algorithm.HMAC256(key);
			
			UserModel user = (UserModel) auth.getPrincipal();
			
			
			return JWT.create()
					.withIssuer(userGenerator)
					.withSubject(user.getUsername())
					.withClaim("id", user.getId())
					.withIssuedAt(new Date())
					.withExpiresAt(new Date(System.currentTimeMillis() + 43200000))
					.withJWTId(UUID.randomUUID().toString())
					.withNotBefore(new Date(System.currentTimeMillis()))
					.sign(alg);
			
		}
	
	public DecodedJWT validateToken(String token) {
		
		try {
			
			Algorithm alg = Algorithm.HMAC256(key);
			
			JWTVerifier verifier = JWT.require(alg)
					.withIssuer(userGenerator)
					.build();
			
			return verifier.verify(token);
		}catch(JWTVerificationException e) {
			throw new JWTVerificationException("Error verifying the token");
		}
		
	}

	public String getUsername(DecodedJWT token) {
		return token.getSubject().toString();
	}
	
	public Claim getSpecificClaim(DecodedJWT token, String claim) {
		return token.getClaim(claim);
	}
	
	public Map<String,Claim> getAllClaims(DecodedJWT token){
		return token.getClaims();
	}
	
}
