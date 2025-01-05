package com.wnc.banking.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Getter
@Component
public class JwtTokenProvider {
    private final String SECRET_KEY = "AdvancedWebApplicationDevelopment/CSC13114/Group1/HCMUS";
    private final long JWT_EXPIRATION = 3600 * 60;
    public final long REFRESH_EXPIRATION = 7 * 24 * 60 * 60;

    public String generateAccessToken(String email, String role) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.SECOND, (int) JWT_EXPIRATION);
        Date expirationDate = calendar.getTime();

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
