package com.microservices.authservice.helper;

import com.microservices.authservice.client.EmployeeServiceClient;
import com.microservices.authservice.dto.EmployeeAuthResponse;
import com.microservices.authservice.exception.EmployeeServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmployeeServiceHelper
{
    @Autowired
    private EmployeeServiceClient employeeServiceClient;

    @Retry(name = "employeeservice")
    @CircuitBreaker(name = "employeeservice", fallbackMethod = "getEmployeeAuthResponseFallback")
    public EmployeeAuthResponse getEmployeeAuthResponse(Long userId)
    {
        return employeeServiceClient.getEmployeeAuthResponse(userId);
    }

    public EmployeeAuthResponse getEmployeeAuthResponseFallback(Long userId, Exception exception)
    {
        log.error("Circuit breaker fallback triggered during login for userId: {}", userId, exception);
        throw new EmployeeServiceUnavailableException("Unable to process login because Employee Service is unavailable");
    }
}
