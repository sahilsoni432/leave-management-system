package com.microservices.apigateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class FallbackController
{
    @RequestMapping("/fallback/auth-service")
    public ResponseEntity<Map<String, Object>> authServiceFallback()
    {
        log.error("Fallback triggered for AUTHSERVICE");
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "satus", "SERVICE_UNAVAILABLE",
                        "service", "AUTHSERVICE",
                        "message", "Auth service is currently unavailable"
                ));
    }

    @RequestMapping("/fallback/employee-service")
    public ResponseEntity<Map<String, Object>> employeeServiceFallback()
    {
        log.error("Fallback triggered for EMPLOYEESERVICE");
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "satus", "SERVICE_UNAVAILABLE",
                        "service", "EMPLOYEESERVICE",
                        "message", "Employee service is currently unavailable"
                ));
    }

    @RequestMapping("/fallback/leave-service")
    public ResponseEntity<Map<String, Object>> leaveServiceFallback()
    {
        log.error("Fallback triggered for LEAVESERVICE");
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "satus", "SERVICE_UNAVAILABLE",
                        "service", "LEAVESERVICE",
                        "message", "Leave service is currently unavailable"
                ));
    }
}
