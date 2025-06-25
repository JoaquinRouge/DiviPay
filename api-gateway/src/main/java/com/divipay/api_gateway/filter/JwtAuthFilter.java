package com.divipay.api_gateway.filter;

import org.springframework.stereotype.Component;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.divipay.api_gateway.utils.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter {

    private JwtUtils jwtUtils;
    
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

        String userId = decoded.getClaim("id").asString();
        String email = decoded.getClaim("sub").asString();
        String hasPaid = decoded.getClaim("hasPaid").asString();

        // Agregar headers personalizados
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-User-Id", userId)
            .header("X-Email", email)
            .header("X-Has-Paid", hasPaid)
            .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}

