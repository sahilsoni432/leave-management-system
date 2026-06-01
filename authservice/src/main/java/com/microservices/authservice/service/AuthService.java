package com.microservices.authservice.service;

import com.microservices.authservice.client.EmployeeServiceClient;
import com.microservices.authservice.dto.EmployeeAuthResponse;
import com.microservices.authservice.dto.LoginRequest;
import com.microservices.authservice.dto.LoginResponse;
import com.microservices.authservice.entity.User;
import com.microservices.authservice.exception.EmployeeServiceUnavailableException;
import com.microservices.authservice.exception.UnauthorizedException;
import com.microservices.authservice.repository.UserRepository;
import com.microservices.authservice.util.JwtUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService
{
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeServiceClient employeeServiceClient;

    @Retry(name = "employeeservice")
    @CircuitBreaker(name = "employeeservice", fallbackMethod = "loginFallback")
    public LoginResponse login(LoginRequest request)
    {
        log.info("Authenticating user with email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if(!user.getPassword().equals(request.getPassword()))
        {
            log.error("Invalid password for email: {}", request.getEmail());
           throw new  UnauthorizedException("Invalid email or password");
        }

        EmployeeAuthResponse employeeAuthResponse = getEmployeeAuthResponse(user.getId());
        String token = jwtUtil.generateToken(user.getId(), employeeAuthResponse.getRole(),
                employeeAuthResponse.getEmployeeId());
        log.info("JWT token generated successfully for userId: {}", user.getId());

        return new LoginResponse(token, employeeAuthResponse.getRole(), user.getId());
    }

    public LoginResponse loginFallback(LoginRequest request, Exception exception)
    {
        log.error("Circuit breaker fallback triggered during login for email: {}", request.getEmail(), exception);
        throw new EmployeeServiceUnavailableException("Unable to process login because Employee Service is unavailable");
    }

    private EmployeeAuthResponse getEmployeeAuthResponse(Long userId)
    {
        log.info("Fetching employee auth details for userId: {}", userId);
        return employeeServiceClient.getEmployeeAuthResponse(userId);
    }

    public boolean isUserExists(Long userId)
    {
        return userRepository.existsById(userId);
    }
}
