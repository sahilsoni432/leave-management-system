package com.microservices.apigateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtUtil
{
    @Value("${jwt.secret-key}")
    private String secretKey;

    private SecretKey getSignKey()
    {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean validateToken(String token)
    {
        try{
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);
            log.info("JWT token validated successfully");
            return true;
        }
        catch (Exception e)
        {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractRole(String token)
    {
        log.info("Extracting role from JWT token");
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role")
                .toString();
    }

    public Long extractEmployeeId(String token)
    {
        log.info("Extracting employeeId from JWT token");
        return Long.valueOf(Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("employeeId")
                .toString());
    }

    public Long extractUserId(String token)
    {
        log.info("Extracting userId from JWT token");
        return Long.valueOf(Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId")
                .toString());
    }
}
