package com.e_commerce.api_gateway_service.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Skip authentication for public endpoints (login & register)
        String path = request.getURI().getPath();
        if (path.contains("user-service/auth/login")
                || path.contains("user-service/users")
                || path.contains("product-service/products")) {
            return chain.filter(exchange);
        }

        // Cek apakah ada Authorization Header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        String token = authHeader.substring(7); // Menghapus "Bearer " dari token
        try {
            Claims claims = jwtUtil.validateToken(token); // ✅ Claims sudah benar
            String userId = claims.getSubject(); // ✅ Ambil userId dari token

            // Tambahkan userId ke header agar diteruskan ke service lain
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("userId", userId)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
    }
}
