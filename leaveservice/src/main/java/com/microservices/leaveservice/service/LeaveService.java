package com.microservices.leaveservice.service;

import com.microservices.leaveservice.dto.*;
import com.microservices.leaveservice.entity.LeaveRequest;
import com.microservices.leaveservice.enums.LeaveStatus;
import com.microservices.leaveservice.enums.LeaveType;
import com.microservices.leaveservice.enums.NotificationEventType;
import com.microservices.leaveservice.exception.BadRequestException;
import com.microservices.leaveservice.exception.ForbiddenException;
import com.microservices.leaveservice.exception.ResourceNotFoundException;
import com.microservices.leaveservice.helper.EmployeeServiceHelper;
import com.microservices.leaveservice.producer.NotificationProducer;
import com.microservices.leaveservice.repository.LeaveRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class LeaveService
{
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeServiceHelper employeeServiceHelper;

    @Autowired
    private NotificationProducer notificationProducer;

    public LeaveRequest applyLeave(ApplyLeaveRequest request, Long employeeId)
    {
        if (request.getStartDate().isBefore(LocalDate.now())) {
            log.error("Start date is a past date. EmployeeId: {}", employeeId);
            throw new BadRequestException("Past dates are not allowed");
        }

        if (request.getStartDate().isAfter(request.getEndDate()))
        {
            log.error("Start date cannot be after end date. EmployeeId: {}", employeeId);
            throw new BadRequestException("Start date cannot be after end date");
        }

        log.info("Checking overlapping leaves if exists for employeeId: {}", employeeId);
        boolean overlapExists = leaveRequestRepository.existsByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId,
                request.getEndDate(),
                request.getStartDate()
        );

        if(overlapExists)
        {
            log.error("Overlapping leave request already exists for employeeId: {}, leave startDate: {}, leave endDate: {}, leaveType: {}",
                    employeeId, request.getStartDate(), request.getEndDate(), request.getLeaveType().name());
            throw new BadRequestException("Overlapping leave request exists");
        }

        log.info("Fetching remaining leave balance for employeeId: {} and leaveType: {}", employeeId, request.getLeaveType().name());
        LeaveBalanceResponse leaveBalanceResponse = employeeServiceHelper.getEmployeeLeaveBalance(employeeId);

        int remainingLeaves = getRemainingLeaves(request, leaveBalanceResponse);
        log.info("Remaining leaves count: {}, employeeId: {}, leaveType: {}", remainingLeaves, employeeId, request.getLeaveType().name());

        if(request.getNumberOfDays() > remainingLeaves)
        {
            log.error("Insufficient leave balance for employeeId: {}, remaining leave: {}, leave requested: {}",
                    employeeId, remainingLeaves, request.getNumberOfDays());
            throw new BadRequestException("Insufficient leave balance");
        }

        LeaveRequest leaveRequest = new LeaveRequest();

        leaveRequest.setEmployeeId(employeeId);
        leaveRequest.setManagerId(request.getManagerId());
        leaveRequest.setLeaveType(request.getLeaveType());
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setNumberOfDays(request.getNumberOfDays());
        leaveRequest.setReason(request.getReason());
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setAppliedDate(LocalDate.now());
        leaveRequest.setRejectionReason(null);

        log.info("Leave successfully applied for employeeId: {}, for manager: {}, leaveType: {}, startDate: {}, endDate: {}, numberOfDays: {}, reason: {}, status: {}",
                employeeId, leaveRequest.getManagerId(), leaveRequest.getLeaveType().name(), leaveRequest.getStartDate(), leaveRequest.getEndDate(),
                leaveRequest.getNumberOfDays(), leaveRequest.getReason(), leaveRequest.getStatus().name());

        leaveRequest = leaveRequestRepository.save(leaveRequest);
        triggerLeaveEventNotification(leaveRequest, NotificationEventType.LEAVE_APPLIED);
        return leaveRequest;
    }

    private int getRemainingLeaves(ApplyLeaveRequest request, LeaveBalanceResponse leaveBalance)
    {
        int remainingLeaves = 0;

        if(LeaveType.CASUAL.equals(request.getLeaveType()))
        {
            remainingLeaves = leaveBalance.getCasualRemaining();
        }
        else if (LeaveType.SICK.equals(request.getLeaveType()))
        {
            remainingLeaves = leaveBalance.getSickRemaining();
        }
        else if (LeaveType.PRIVILEGE.equals(request.getLeaveType()))
        {
            remainingLeaves = leaveBalance.getPrivilegeRemaining();
        }
        return remainingLeaves;
    }

    private void triggerLeaveEventNotification(LeaveRequest leaveRequest, NotificationEventType eventType)
    {
        try{
            log.info("Fetching employee details for notification for employeeId: {}", leaveRequest.getEmployeeId());
            EmployeeResponse employeeResponse = employeeServiceHelper.getEmployeeByIdForNotification(leaveRequest.getEmployeeId());

            NotificationEvent event = new NotificationEvent();

            event.setEventType(eventType.name());
            event.setEmployeeId(leaveRequest.getEmployeeId());
            event.setEmployeeName(employeeResponse.getEmployeeName());
            event.setEmployeeEmail(employeeResponse.getEmployeeEmail());
            event.setEmployeeRole(employeeResponse.getRole());
            event.setManagerId(employeeResponse.getManagerId());
            event.setLeaveId(leaveRequest.getId());
            event.setStartDate(leaveRequest.getStartDate());
            event.setEndDate(leaveRequest.getEndDate());
            event.setNumberOfDays(leaveRequest.getNumberOfDays());
            event.setLeaveStatus(leaveRequest.getStatus().name());
            event.setReason(leaveRequest.getReason());
            event.setRejectionReason(leaveRequest.getRejectionReason());
            event.setAppliedDate(leaveRequest.getAppliedDate());
            setNotificationEventMessage(eventType, event);
            event.setTimestamp(LocalDateTime.now());

            notificationProducer.sendNotification(event);

            log.info("{} notification sent successfully for leaveId: {} and employeeId: {}",
                    eventType, leaveRequest.getId(), leaveRequest.getEmployeeId());
        }
        catch (Exception e)
        {
            log.error("Failed to send {} notification for leaveId: {}, employeeId: {}",
                    eventType, leaveRequest.getId(), leaveRequest.getEmployeeId(), e);
        }
    }

    private void setNotificationEventMessage(NotificationEventType eventType, NotificationEvent event)
    {
        if(NotificationEventType.LEAVE_APPLIED.equals(eventType))
        {
            event.setMessage("Leave applied successfully");
        }
        else if(NotificationEventType.LEAVE_APPROVED.equals(eventType))
        {
            event.setMessage("Leave approved successfully");
        }
        else if(NotificationEventType.LEAVE_REJECTED.equals(eventType))
        {
            event.setMessage("Leave rejected");
        }
    }

    public LeaveRequest approveLeave(Long leaveId, Long managerId)
    {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> {
                    log.error("Leave request not found for leaveId: {}", leaveId);
                    return new ResourceNotFoundException("Leave request not found");
                });

        if(!leaveRequest.getManagerId().equals(managerId))
        {
            log.error("Logged in managerId is not same as manager for leave to be approved. LoggedIn manager: {}, leave manager: {}",
                    managerId, leaveRequest.getManagerId());
            throw new ForbiddenException("You cannot approve another manager's leave requests");
        }

        if(!LeaveStatus.PENDING.equals(leaveRequest.getStatus()))
        {
            log.error("Leave is already processed with id: {} and status: {}, managerId: {}",
                    leaveId,leaveRequest.getStatus().name(), leaveRequest.getManagerId());
            throw new BadRequestException("Leave already processed");
        }

        DeductLeaveRequest deductLeaveRequest = new DeductLeaveRequest(leaveRequest.getEmployeeId(),
                leaveRequest.getLeaveType().name(), leaveRequest.getNumberOfDays());

        employeeServiceHelper.deductEmployeeLeaveBalance(deductLeaveRequest);

        log.info("Leave is successfully approved by manager: {}, employeeId: {}, leaveId: {}",
                leaveRequest.getManagerId(), leaveRequest.getEmployeeId(), leaveRequest.getId());
        leaveRequest.setStatus(LeaveStatus.APPROVED);

        leaveRequest = leaveRequestRepository.save(leaveRequest);
        triggerLeaveEventNotification(leaveRequest, NotificationEventType.LEAVE_APPROVED);
        return leaveRequest;
    }

    public LeaveRequest rejectLeave(Long leaveId, String reason, Long managerId)
    {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> {
                    log.error("Leave request not found for leaveId: {}", leaveId);
                    return new ResourceNotFoundException("Leave request not found");
                });

        if(!leaveRequest.getManagerId().equals(managerId))
        {
            log.error("Logged in manager is not same as leave manger. LoggedIn: {}, leave Manager: {}", managerId, leaveRequest.getManagerId());
            throw new ForbiddenException("You cannot reject another manager's leave requests");
        }

        if(!LeaveStatus.PENDING.equals(leaveRequest.getStatus()))
        {
            log.error("Leave is already processed with id: {} and status: {}, managerId: {}",
                    leaveId,leaveRequest.getStatus().name(), leaveRequest.getManagerId());
            throw new BadRequestException("Leave already processed");
        }

        if(reason == null || reason.isBlank())
        {
            log.error("Rejection reason cannot be null for leaveId: {}, managerId: {}", leaveId, managerId);
            throw new BadRequestException("Rejection reason is mandatory");
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setRejectionReason(reason);
        log.info("Leave successfully rejected for employeeId: {}, leaveId: {} by managerId: {}",
                leaveRequest.getEmployeeId(), leaveId, leaveRequest.getManagerId());

        leaveRequest = leaveRequestRepository.save(leaveRequest);
        triggerLeaveEventNotification(leaveRequest, NotificationEventType.LEAVE_REJECTED);
        return leaveRequest;
    }

    public List<ManagerLeaveResponse> getManagerLeaves(Long managerId, final ManagerLeaveRequest managerLeaveRequest)
    {
        List<LeaveRequest> leaveRequests =  leaveRequestRepository.findByManagerId(managerId);

        return leaveRequests.stream()
                .filter(leave -> managerLeaveRequest.getStatus() == null ||
                        managerLeaveRequest.getStatus().equalsIgnoreCase("ALL") ||
                        leave.getStatus().name().equals(managerLeaveRequest.getStatus()))
                .filter(leave -> managerLeaveRequest.getEmployeeId() == null ||
                        leave.getEmployeeId().equals(managerLeaveRequest.getEmployeeId()))
                .filter(leave -> managerLeaveRequest.getFromDate() == null ||
                        !leave.getStartDate().isBefore(managerLeaveRequest.getFromDate()))
                .filter(leave -> managerLeaveRequest.getToDate() == null ||
                        !leave.getEndDate().isAfter(managerLeaveRequest.getToDate()))
                .map(leave -> {
                    try{
                        log.info("Fetching employee details for employeeId: {}", leave.getEmployeeId());
                        EmployeeResponse employeeResponse = employeeServiceHelper.getEmployeeByIdForManager(leave.getEmployeeId());
                        return getManagerLeaveResponse(leave, employeeResponse);
                    }
                    catch (Exception e){
                        log.error("Error occurred while fetching employee details for employeeId: {}",
                                leave.getEmployeeId(), e);
                    }
                    return null;
                }).toList();
    }

    private ManagerLeaveResponse getManagerLeaveResponse(LeaveRequest leave, EmployeeResponse employeeResponse)
    {
        ManagerLeaveResponse response = new ManagerLeaveResponse();
        response.setLeaveId(leave.getId());
        response.setEmployeeId(employeeResponse.getEmployeeId());
        response.setEmployeeName(employeeResponse.getEmployeeName());
        response.setEmployeeEmail(employeeResponse.getEmployeeEmail());
        response.setLeaveType(leave.getLeaveType().name());
        response.setStatus(leave.getStatus().name());
        response.setStartDate(leave.getStartDate());
        response.setEndDate(leave.getEndDate());
        response.setAppliedDate(leave.getAppliedDate());
        response.setNumberOfDays(leave.getNumberOfDays());
        response.setReason(leave.getReason());
        response.setRejectionReason(leave.getRejectionReason());
        return response;
    }

    public Page<LeaveRequest> getMyLeaveHistory(Long employeeId, LeaveHistoryRequest request)
    {
        log.info("Fetching leave request for employeeId: {}", employeeId);
        List<LeaveRequest>  leaveRequests = leaveRequestRepository.findByEmployeeId(employeeId);

        List<LeaveRequest> filteredLeaves = leaveRequests.stream()
                .filter(leave -> request.getStatus() == null ||
                        request.getStatus().equalsIgnoreCase("ALL") ||
                        leave.getStatus().name().equalsIgnoreCase(request.getStatus()))
                .filter(leave -> request.getLeaveType() == null ||
                        request.getLeaveType().equalsIgnoreCase("ALL") ||
                        leave.getLeaveType().name().equalsIgnoreCase(request.getLeaveType()))
                .filter(leave -> request.getFromDate() == null ||
                        !leave.getStartDate().isBefore(request.getFromDate()))
                .filter(leave -> request.getToDate() == null ||
                        !leave.getEndDate().isAfter(request.getToDate()))
                .toList();

        int start = request.getPage() * request.getSize();
        int end = Math.min(start + request.getSize(), filteredLeaves.size());

        List<LeaveRequest> paginatedLeaves = start > filteredLeaves.size() ? Collections.emptyList() : filteredLeaves.subList(start, end);
        log.info("Returning {} leave records for employeeId: {}", paginatedLeaves.size(), employeeId);

        return new PageImpl<>(paginatedLeaves, PageRequest.of(request.getPage(),
                request.getSize()), filteredLeaves.size());
    }
}
