package com.wnc.banking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Date;

public class JwtUtil {
    private static String secretKey = "AdvancedWebApplicationDevelopment/CSC13114/Group1/HCMUS";

    public static final String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username, String role) {
        final String extractedUsername = extractEmail(token);
        final String extractedRole = extractRole(token);
        return (extractedUsername.equals(username) && extractedRole.equals(role) && !isTokenExpired(token));
    }

    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}
