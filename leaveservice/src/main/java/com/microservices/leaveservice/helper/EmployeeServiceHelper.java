package com.microservices.leaveservice.helper;

import com.microservices.leaveservice.client.EmployeeServiceClient;
import com.microservices.leaveservice.dto.*;
import com.microservices.leaveservice.exception.EmployeeServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmployeeServiceHelper
{
    @Autowired
    private EmployeeServiceClient employeeServiceClient;

    @Retry(name = "employeeservice")
    @CircuitBreaker(name = "employeeservice", fallbackMethod = "getEmployeeLeaveBalanceFallback")
    public LeaveBalanceResponse getEmployeeLeaveBalance(Long employeeId)
    {
        return employeeServiceClient.getEmployeeLeaveBalance(employeeId);
    }

    public LeaveBalanceResponse getEmployeeLeaveBalanceFallback(Long employeeId, Exception exception)
    {
        log.error("Circuit breaker fallback triggered while fetching leave balance for employeeId: {}", employeeId, exception);
        throw new EmployeeServiceUnavailableException("Unable to process leave request because Employee Service is unavailable");
    }

    @Retry(name = "employeeservice")
    @CircuitBreaker(name = "employeeservice", fallbackMethod = "deductEmployeeLeaveBalanceFallback")
    public Boolean deductEmployeeLeaveBalance(DeductLeaveRequest deductLeaveRequest)
    {
        return employeeServiceClient.deductLeave(deductLeaveRequest);
    }

    public Boolean deductEmployeeLeaveBalanceFallback(DeductLeaveRequest deductLeaveRequest, Exception exception)
    {
        log.error("Circuit breaker fallback triggered while deducting leave balance for employeeId: {}",
                deductLeaveRequest.getEmployeeId(), exception);
        throw new EmployeeServiceUnavailableException("Unable to deduct leave balance because Employee Service is unavailable");
    }

    @Retry(name = "employeeservice")
    @CircuitBreaker(name = "employeeservice", fallbackMethod = "getEmployeeByIdForManagerFallback")
    public EmployeeResponse getEmployeeByIdForManager(Long employeeId)
    {
        return employeeServiceClient.getEmployeeById(employeeId);
    }

    public List<ManagerLeaveResponse> getEmployeeByIdForManagerFallback(Long employeeId, Exception exception)
    {
        log.error("Circuit breaker fallback triggered for employeeId: {}", employeeId, exception);
        throw new EmployeeServiceUnavailableException("Unable to get Employee details because Employee Service is currently unavailable");
    }

    public EmployeeResponse getEmployeeByIdForNotification(Long employeeId)
    {
        return employeeServiceClient.getEmployeeById(employeeId);
    }
}
