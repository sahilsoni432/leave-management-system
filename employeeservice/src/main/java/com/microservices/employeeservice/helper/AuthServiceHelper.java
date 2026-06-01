package com.microservices.employeeservice.helper;

import com.microservices.employeeservice.client.AuthServiceClient;
import com.microservices.employeeservice.exception.AuthServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceHelper
{
    @Autowired
    private AuthServiceClient authServiceClient;

    @Retry(name = "authservice")
    @CircuitBreaker(name = "authservice", fallbackMethod = "isUserExistsFallback")
    public Boolean isUserExists(Long userId)
    {
        return authServiceClient.isUserExists(userId);
    }

    public Boolean isUserExistsFallback(Long userId, Exception exception)
    {
        log.error("Circuit breaker fallback triggered while checking user existence for userId: {}", userId, exception);
        throw new AuthServiceUnavailableException("Unable to check user existence because AUTH service is unavailable");
    }
}
