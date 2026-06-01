package com.microservices.employeeservice.controller;

import com.microservices.employeeservice.annotations.RoleAllowed;
import com.microservices.employeeservice.constants.ApplicationConstants;
import com.microservices.employeeservice.dto.*;
import com.microservices.employeeservice.entity.Employee;
import com.microservices.employeeservice.enums.Role;
import com.microservices.employeeservice.exception.ForbiddenException;
import com.microservices.employeeservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Employee APIs",
description = "APIs related to employee management and leave balances")
@RestController
@RequestMapping("/employees")
public class EmployeeController
{
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/create")
    @Operation(summary = "Create employee profile")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody CreateEmployeeRequest request, HttpServletRequest httpServletRequest)
    {
        String authHeader = httpServletRequest.getHeader(ApplicationConstants.AUTHORIZATION);

        if(authHeader != null && authHeader.startsWith(ApplicationConstants.BEARER))
        {
            log.error("Logged-in users cannot create employees");
            throw new ForbiddenException("Logged-in users cannot create employees");
        }

        log.info("Create employee request received for email: {}", request.getEmail());
        Employee employee = employeeService.createEmployee(request);
        log.info("Employee created successfully for email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @GetMapping("/my-leave-balance")
    @Operation(summary = "Returns leave balances for all leave types including allocated, user and remaining leaves")
    @RoleAllowed(Role.ROLE_EMPLOYEE)
    public ResponseEntity<LeaveBalanceResponse> getLeaveBalance(HttpServletRequest request)
    {
        Long employeeId = Long.valueOf(request.getHeader(ApplicationConstants.X_EMPLOYEE_ID));
        log.info("Fetching leave balance for employeeId: {}", employeeId);
        LeaveBalanceResponse leaveBalanceResponse = employeeService.getLeaveBalance(employeeId);
        return ResponseEntity.ok(leaveBalanceResponse);
    }

    @GetMapping("/{employeeId}/leave-balance")
    @Operation(summary = "Internal API - Get employee leave balance")
    @RoleAllowed(Role.ROLE_EMPLOYEE)
    public ResponseEntity<LeaveBalanceResponse> getEmployeeLeaveBalance(@PathVariable Long employeeId,
                                                                        HttpServletRequest request)
    {
        log.info("Fetching leave balance for employeeId: {}", employeeId);
        Long loggedInEmployeeId = Long.valueOf(request.getHeader(ApplicationConstants.X_EMPLOYEE_ID));

        if(!employeeId.equals(loggedInEmployeeId))
        {
            log.error("Loggenin employeeId is not equals to the requested employeeId. " +
                    "Loggedin : {} and Requested: {}",loggedInEmployeeId, employeeId );
            throw new ForbiddenException("You cannot access another employee's leave balance");
        }

        LeaveBalanceResponse leaveBalanceResponse = employeeService.getLeaveBalance(employeeId);
        return ResponseEntity.ok(leaveBalanceResponse);
    }

    @PutMapping("/deduct-leave")
    @Operation(summary = "Internal API - Deduct employee leave balance")
    @RoleAllowed(Role.ROLE_MANAGER)
    public ResponseEntity<Boolean> deductLeave(@Valid @RequestBody DeductLeaveRequest request, HttpServletRequest httpServletRequest)
    {
        log.info("Deduct leave request received for employeeId: {}", request.getEmployeeId());
        boolean isLeaveDeducted = employeeService.deductLeave(request);
        log.info("Leave deducted successfully for employeeId: {}", employeeService);
        return ResponseEntity.ok(isLeaveDeducted);
    }

    @GetMapping("/auth/{userId}")
    @Operation(summary = "Internal API - Get employee authentication details")
    public ResponseEntity<EmployeeAuthResponse> getEmployeeAuthDetails(@PathVariable Long userId)
    {
        log.info("Fetching employee auth details for userId: {}", userId);
        EmployeeAuthResponse response = employeeService.getEmployeeAuthDetails(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Internal API - Get employee details")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable Long employeeId)
    {
        log.info("Fetching employee response for employeeId: {}", employeeId);
        return ResponseEntity.ok(employeeService.getEmployee(employeeId));
    }
}
