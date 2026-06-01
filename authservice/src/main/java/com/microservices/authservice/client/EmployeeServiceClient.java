package com.microservices.authservice.client;

import com.microservices.authservice.dto.EmployeeAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "EMPLOYEESERVICE")
public interface EmployeeServiceClient
{
    @GetMapping("/employees/auth/{userId}")
    EmployeeAuthResponse getEmployeeAuthResponse(@PathVariable("userId") Long userId);

}
