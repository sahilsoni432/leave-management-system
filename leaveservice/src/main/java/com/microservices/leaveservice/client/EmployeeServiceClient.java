package com.microservices.leaveservice.client;

import com.microservices.leaveservice.config.FeignConfig;
import com.microservices.leaveservice.dto.DeductLeaveRequest;
import com.microservices.leaveservice.dto.EmployeeResponse;
import com.microservices.leaveservice.dto.LeaveBalanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "EMPLOYEESERVICE", configuration = FeignConfig.class)
public interface EmployeeServiceClient
{
    @GetMapping("/employees/{employeeId}/leave-balance")
    LeaveBalanceResponse getEmployeeLeaveBalance(@PathVariable("employeeId") Long employeeId);

    @PutMapping("/employees/deduct-leave")
    Boolean deductLeave(@RequestBody DeductLeaveRequest request);

    @GetMapping("/employees/{employeeId}")
    EmployeeResponse getEmployeeById(@PathVariable Long employeeId);
}
