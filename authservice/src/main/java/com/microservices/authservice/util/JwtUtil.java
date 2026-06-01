package com.microservices.authservice.util;

import com.microservices.authservice.constants.ApplicationConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil
{
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    public String generateToken(Long userId, String role, Long employeeId)
    {
        log.info("Generating JWT token for userId: {}, role: {}, employeeId: {}",
                userId, role, employeeId);
        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .claim("employeeId", employeeId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey())
                .compact();
    }

    public String extractRole(String token)
    {
        log.info("Extracting role from JWT token");
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(ApplicationConstants.ROLE)
                .toString();
    }

    public boolean validateToken(String token)
    {
        try{
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);
            log.info("JWT token validation successful");
            return true;
        }
        catch (Exception e){
            log.error("JWT token validation unsuccessful");
            return false;
        }
    }

    private SecretKey getSignKey()
    {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
