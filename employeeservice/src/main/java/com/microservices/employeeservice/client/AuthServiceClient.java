package com.microservices.employeeservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "AUTHSERVICE")
public interface AuthServiceClient
{
    @GetMapping("/auth/users/{userId}/exists")
    Boolean isUserExists(@PathVariable("userId") Long userId);
}
