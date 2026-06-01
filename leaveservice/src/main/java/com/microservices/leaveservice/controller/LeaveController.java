package com.microservices.leaveservice.controller;

import com.microservices.leaveservice.annotation.RoleAllowed;
import com.microservices.leaveservice.constants.ApplicationConstants;
import com.microservices.leaveservice.dto.ApplyLeaveRequest;
import com.microservices.leaveservice.dto.LeaveHistoryRequest;
import com.microservices.leaveservice.dto.ManagerLeaveRequest;
import com.microservices.leaveservice.dto.ManagerLeaveResponse;
import com.microservices.leaveservice.entity.LeaveRequest;
import com.microservices.leaveservice.enums.Role;
import com.microservices.leaveservice.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Leave APIs",
description = "APIs related to leave management workflow")
@RestController
@RequestMapping("/leaves")
public class LeaveController
{
    @Autowired
    private LeaveService leaveService;

    @Operation(summary = "Apply leave request")
    @PostMapping("/apply")
    @RoleAllowed(Role.ROLE_EMPLOYEE)
    public ResponseEntity<LeaveRequest> applyLeave(@Valid @RequestBody ApplyLeaveRequest request, HttpServletRequest httpServletRequest)
    {
        Long employeeId = Long.valueOf(httpServletRequest.getHeader(ApplicationConstants.X_EMPLOYEE_ID));
        log.info("Apply leave request received for employeeId: {}", employeeId);
        LeaveRequest leaveRequest = leaveService.applyLeave(request, employeeId);

        log.info("Leave applied successfully for employeeId: {}", employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequest);
    }

    @Operation(summary = "Get requested type of leaves with employee details for manager")
    @GetMapping("/manager")
    @RoleAllowed(Role.ROLE_MANAGER)
    public ResponseEntity<List<ManagerLeaveResponse>> getManagerLeaves(HttpServletRequest request,
                                               @ModelAttribute ManagerLeaveRequest managerLeaveRequest)
    {
        Long managerId = Long.valueOf(request.getHeader(ApplicationConstants.X_EMPLOYEE_ID));
        log.info("Fetching employees leaves for manager with managerId: {}", managerId);
        List<ManagerLeaveResponse> responseList = leaveService.getManagerLeaves(managerId, managerLeaveRequest);
        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "Approve employee leave request")
    @PutMapping("/{leaveId}/approve")
    @RoleAllowed(Role.ROLE_MANAGER)
    public ResponseEntity<LeaveRequest> approveLeave(@PathVariable Long leaveId, HttpServletRequest request)
    {
        Long managerId = Long.valueOf(request.getHeader(ApplicationConstants.X_EMPLOYEE_ID));
        log.info("Leave approval request received by manangerId: {}, for leaveId: {}", managerId, leaveId);

        LeaveRequest leaveRequest = leaveService.approveLeave(leaveId, managerId);
        log.info("Leave with id: {} approved successfully ", leaveId);
        return ResponseEntity.ok(leaveRequest);
    }

    @PutMapping("/{leaveId}/reject")
    @Operation(summary = "reject employee leave request")
    @RoleAllowed(Role.ROLE_MANAGER)
    public ResponseEntity<LeaveRequest> rejectLeave(@PathVariable Long leaveId,
                                                    @NotBlank @RequestParam String reason,
                                                    HttpServletRequest request)
    {
        Long managerId = Long.valueOf(request.getHeader(ApplicationConstants.X_EMPLOYEE_ID));
        log.info("Leave rejection request received by managerId: {} and leaveId: {}", managerId, leaveId);
        LeaveRequest leaveRequest = leaveService.rejectLeave(leaveId, reason, managerId);
        log.info("Leave rejected successfully for leaveId: {} and by manager: {}", leaveId, managerId);
        return ResponseEntity.ok(leaveRequest);
    }

    @Operation(summary = "Get logged-in employee leave history")
    @GetMapping("my-leave-history")
    @RoleAllowed(Role.ROLE_EMPLOYEE)
    public ResponseEntity<Page<LeaveRequest>> getMyLeaveHistory(HttpServletRequest request,
                                                                @ModelAttribute LeaveHistoryRequest leaveHistoryRequest)
    {
        Long employeeId = Long.valueOf(request.getHeader(ApplicationConstants.X_EMPLOYEE_ID));
        log.info("Leave balance request received for employeeId: {}", employeeId);
        Page<LeaveRequest> leaveRequests = leaveService.getMyLeaveHistory(employeeId, leaveHistoryRequest);
        return ResponseEntity.ok(leaveRequests);
    }
}
