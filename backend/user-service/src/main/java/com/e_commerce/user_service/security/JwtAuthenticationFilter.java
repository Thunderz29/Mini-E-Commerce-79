package com.e_commerce.user_service.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("JWT Filter Active");

        String token = getJwtFromRequest(request);
        System.out.println("Extracted Token: " + token);

        if (StringUtils.hasText(token)) {
            if (jwtTokenProvider.validateToken(token)) {
                Claims claims = jwtTokenProvider.getClaims(token);
                String userId = claims.getSubject(); // Ambil userId dari token
                String email = claims.get("email", String.class);

                System.out.println("Token User ID: " + userId);
                System.out.println("Token Email: " + email);

                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    JwtAuthenticationToken authentication = new JwtAuthenticationToken(userDetails, token,
                            userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("User Authenticated Successfully: " + email);
                } catch (Exception e) {
                    System.out.println("Authentication Failed: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid Token");
            }
        } else {
            System.out.println("No Token Found in Request");
        }

        chain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
