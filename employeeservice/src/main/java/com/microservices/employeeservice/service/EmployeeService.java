package com.microservices.employeeservice.service;

import com.microservices.employeeservice.dto.*;
import com.microservices.employeeservice.entity.Employee;
import com.microservices.employeeservice.entity.LeaveBalance;
import com.microservices.employeeservice.enums.LeaveType;
import com.microservices.employeeservice.enums.Role;
import com.microservices.employeeservice.exception.BadRequestException;
import com.microservices.employeeservice.exception.ResourceNotFoundException;
import com.microservices.employeeservice.helper.AuthServiceHelper;
import com.microservices.employeeservice.repository.EmployeeRepository;
import com.microservices.employeeservice.repository.LeaveBalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class EmployeeService
{
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    private AuthServiceHelper authServiceHelper;

    @Value("${leave.allocation.casual}")
    private int casualAllocated;

    @Value("${leave.allocation.sick}")
    private int sickAllocated;

    @Value("${leave.allocation.privilege}")
    private int privilegeAllocated;

    public Employee createEmployee(CreateEmployeeRequest request)
    {
        if(!(Objects.equals(request.getRole(), Role.ROLE_MANAGER.name()) || Objects.equals(request.getRole(), Role.ROLE_EMPLOYEE.name())))
        {
            log.error("Invalid role for creating employee for email: {}", request.getEmail());
            throw new BadRequestException("Invalid role");
        }

        if(Objects.equals(request.getRole(), Role.ROLE_EMPLOYEE.name()) && request.getManagerId() == null)
        {
            log.error("MangerId is null for the employee with email: {}", request.getEmail());
            throw new BadRequestException("Manager Id is required for employees");
        }

        if(Objects.equals(request.getRole(), Role.ROLE_MANAGER.name()) && request.getManagerId() != null)
        {
            log.error("Manager Id should be null for creating manager for email: {}", request.getEmail());
            throw new BadRequestException("Managers should not have manager id");
        }

        if(request.getUserId() != null)
        {
            boolean isUserExists = authServiceHelper.isUserExists(request.getUserId());
            if(!isUserExists){
                log.error("User does not exist for userId: {}", request.getUserId());
                throw new BadRequestException("User does not exist");
            }
        }

        Employee employee = new Employee();

        employee.setUserId(request.getUserId());
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setRole(Role.valueOf(request.getRole()));
        employee.setManagerId(request.getManagerId());

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee successfully created with email: {}", request.getEmail());

        if(Role.ROLE_EMPLOYEE.equals(savedEmployee.getRole()))
        {
            initiateLeaveAllocation(savedEmployee);
        }

        return savedEmployee;
    }

    private void initiateLeaveAllocation(Employee savedEmployee)
    {
        log.info("Initiating leave balance allocation for employee with employeeId: {} and email: {}", savedEmployee.getId(), savedEmployee.getEmail());
        LeaveBalance leaveBalance = new LeaveBalance();

        leaveBalance.setEmployeeId(savedEmployee.getId());

        leaveBalance.setCasualAllocated(casualAllocated);
        leaveBalance.setCasualUsed(0);

        leaveBalance.setSickAllocated(sickAllocated);
        leaveBalance.setSickUsed(0);

        leaveBalance.setPrivilegeAllocated(privilegeAllocated);
        leaveBalance.setPrivilegeUsed(0);

        leaveBalanceRepository.save(leaveBalance);
    }

    public LeaveBalanceResponse getLeaveBalance(Long employeeId)
    {
        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> {
                    log.error("Leave balance not found for employeeId: {}", employeeId);
                    return new ResourceNotFoundException("Leave balance not found");
                });

        LeaveBalanceResponse response = new LeaveBalanceResponse();
        response.setEmployeeId(employeeId);

        response.setCasualAllocated(leaveBalance.getCasualAllocated());
        response.setCasualUsed(leaveBalance.getCasualUsed());
        response.setCasualRemaining(leaveBalance.getCasualAllocated() - leaveBalance.getCasualUsed());

        response.setSickAllocated(leaveBalance.getSickAllocated());
        response.setSickUsed(leaveBalance.getSickUsed());
        response.setSickRemaining(leaveBalance.getSickAllocated() - leaveBalance.getSickUsed());

        response.setPrivilegeAllocated(leaveBalance.getPrivilegeAllocated());
        response.setPrivilegeUsed(leaveBalance.getPrivilegeUsed());
        response.setPrivilegeRemaining(leaveBalance.getPrivilegeAllocated() - leaveBalance.getPrivilegeUsed());

        return response;
    }

    public boolean deductLeave(DeductLeaveRequest request)
    {
        boolean isLeaveDeducted = false;
        boolean isSaveRequired = false;

        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> {
                    log.error("Leave balance not found for employeeId: {}", request.getEmployeeId());
                    return new ResourceNotFoundException("Leave balance not found");
                });

        LeaveType leaveType = LeaveType.valueOf(request.getLeaveType());
        log.info("Deducting leave with details. LeaveType: {}, EmployeeId: {}", leaveType.name(), request.getEmployeeId());

        if(LeaveType.CASUAL.equals(leaveType))
        {
            log.info("Deducting casual leaves for employeeId: {} by {}", request.getEmployeeId(), request.getNumberOfDays());
            leaveBalance.setCasualUsed(leaveBalance.getCasualUsed() + request.getNumberOfDays());
            isSaveRequired = true;
        }
        else if(LeaveType.SICK.equals(leaveType))
        {
            log.info("Deducting sick leaves for employeeId: {} by {}", request.getEmployeeId(), request.getNumberOfDays());
            leaveBalance.setSickUsed(leaveBalance.getSickUsed() + request.getNumberOfDays());
            isSaveRequired = true;
        }
        else if(LeaveType.PRIVILEGE.equals(leaveType))
        {
            log.info("Deducting privilege leaves for employeeId: {} by {}", request.getEmployeeId(), request.getNumberOfDays());
            leaveBalance.setPrivilegeUsed(leaveBalance.getPrivilegeUsed() + request.getNumberOfDays());
            isSaveRequired = true;
        }

        if(isSaveRequired ) {
            leaveBalanceRepository.save(leaveBalance);
            log.info("Leave deducted successfully for employeeId: {}", request.getEmployeeId());
            isLeaveDeducted = true;
        }

        return isLeaveDeducted;
    }

    public EmployeeAuthResponse getEmployeeAuthDetails(Long userId)
    {
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Employee not found for userId: {}", userId);
                    return new ResourceNotFoundException("Employee not found");
                });
        return new EmployeeAuthResponse(employee.getId(), employee.getUserId(), employee.getRole().name());
    }

    public EmployeeResponse getEmployee(Long employeeId)
    {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee not found for employeeId: {}", employeeId);
                    return new ResourceNotFoundException("Employee not found");
                });

        return new EmployeeResponse(employee.getId(), employee.getName(), employee.getEmail(),
                employee.getRole().name(), employee.getManagerId());
    }
}
