// package com.e_commerce.product_service.security;

// import java.security.Key;

// import org.springframework.stereotype.Component;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;

// @Component
// public class JwtTokenProvider {
// private static final String SECRET_KEY =
// "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8";

// public boolean validateToken(String token) {
// try {
// Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
// return true;
// } catch (Exception e) {
// return false;
// }
// }

// private Key getSigningKey() {
// byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
// return Keys.hmacShaKeyFor(keyBytes);
// }
// }
