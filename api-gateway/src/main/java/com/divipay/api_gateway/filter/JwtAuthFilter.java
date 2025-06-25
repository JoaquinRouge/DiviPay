package com.divipay.api_gateway.filter;

import org.springframework.stereotype.Component;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.divipay.api_gateway.utils.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter {

    private JwtUtils jwtUtils;
    
    @Value("${SECRET_SHA}")
    private String secret;
    
    public JwtAuthFilter(JwtUtils jwtUtils) {
    	this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        	 return chain.filter(exchange);
        }

        String token = authHeader.substring(7); // quita "Bearer "

        DecodedJWT decoded;
        try {
            decoded = jwtUtils.validateToken(token);
        } catch (Exception e) {
            return unauthorized(exchange);
        }

        String userId = String.valueOf(decoded.getClaim("id").asInt());
        String email = decoded.getSubject();
        String hasPaid = String.valueOf(decoded.getClaim("hasPaid").asBoolean());

        System.out.println("DATOS: " + userId + hasPaid + email);
        
        String dataToSign = userId + email + hasPaid;
        String signature = null;
        
        try {
			signature = generateHMAC(dataToSign, secret);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        // Agregar headers personalizados
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-User-Id", userId)
            .header("X-Email", email)
            .header("X-Has-Paid", hasPaid)
            .header("X-Signature", signature)
            .build();
      
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
    
    private String generateHMAC(String data, String secretKey) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        
        	
        return Base64.getEncoder().encodeToString(hmacBytes);
    }
    
}

