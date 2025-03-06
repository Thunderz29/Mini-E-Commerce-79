// package com.e_commerce.product_service.security;

// import java.io.IOException;

// import
// org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import
// org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class JwtAuthenticationFilter extends OncePerRequestFilter {

// private final JwtTokenProvider jwtTokenProvider;

// public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
// this.jwtTokenProvider = jwtTokenProvider;
// }

// @Override
// protected void doFilterInternal(HttpServletRequest request,
// HttpServletResponse response,
// FilterChain filterChain) throws ServletException, IOException {
// String token = getJwtFromRequest(request);

// if (token != null && jwtTokenProvider.validateToken(token)) {
// UsernamePasswordAuthenticationToken authentication = new
// UsernamePasswordAuthenticationToken(null, null,
// null);

// authentication.setDetails(new
// WebAuthenticationDetailsSource().buildDetails(request));
// SecurityContextHolder.getContext().setAuthentication(authentication);
// }

// filterChain.doFilter(request, response);
// }

// private String getJwtFromRequest(HttpServletRequest request) {
// String authHeader = request.getHeader("Authorization");
// if (authHeader != null && authHeader.startsWith("Bearer ")) {
// return authHeader.substring(7);
// }
// return null;
// }
// }
