package com.neusoft.elderlycare.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-minutes}")
    private long expirationMinutes;

    private SecretKey cachedKey;

    @PostConstruct
    public void init() {
        this.cachedKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String phone, String role) {
        Date now = new Date();
        Date expires = new Date(now.getTime() + expirationMinutes * 60_000L);
        return Jwts.builder()
                .subject(phone)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expires)
                .signWith(cachedKey)
                .compact();
    }

    public String getPhone(String token) {
        return claims(token).getSubject();
    }

    public boolean validate(String token) {
        try {
            return claims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith(cachedKey).build().parseSignedClaims(token).getPayload();
    }
}
