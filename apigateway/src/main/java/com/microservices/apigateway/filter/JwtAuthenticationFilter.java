package com.microservices.apigateway.filter;

import com.microservices.apigateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered
{
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        String path = exchange.getRequest()
                .getURI()
                .getPath();
        log.info("Incoming request path: {}", path);

        if(path.contains("/auth/login")
                || path.contains("/employees/create")
                || path.contains("/swagger-ui")
                || path.contains("/v3/api-docs")
                || path.contains("swagger-resources"))
        {
            log.info("Bypassing JWT validation for path: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);
            log.error("Authorization header missing or invalid");
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if(!jwtUtil.validateToken(token))
        {
            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);
            log.error("JWT token validation failed");
            return exchange.getResponse().setComplete();
        }

        Long userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);
        Long employeeId = jwtUtil.extractEmployeeId(token);
        log.info("JWT validated successfully for userId: {}, role: {}, employeeId: {}",
                userId, role, employeeId);

        ServerHttpRequest modifiedRequest = exchange.getRequest()
                .mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-Role", role)
                .header("X-Employee-Id", String.valueOf(employeeId))
                .build();

        log.info("Forwarding request with user headers");
        return chain.filter(exchange.mutate()
                .request(modifiedRequest)
                .build());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
