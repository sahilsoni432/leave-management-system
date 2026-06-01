package com.microservices.notificationservice.service;

import com.microservices.notificationservice.dto.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService
{
    public void processNotification(NotificationEvent event)
    {
        log.info("==================== NOTIFICATION RECEIVED ====================");
        log.info("Event Type: {}", event.getEventType());
        log.info("Employee Id: {}", event.getEmployeeId());
        log.info("Employee Name: {}", event.getEmployeeName());
        log.info("Employee Email: {}", event.getEmployeeEmail());
        log.info("Employee role: {}", event.getEmployeeRole());
        log.info("ManagerId: {}", event.getManagerId());
        log.info("Leave Id: {}", event.getLeaveId());
        log.info("Leave start date: {}", event.getStartDate());
        log.info("Leave end date: {}", event.getEndDate());
        log.info("Leave no. of days: {}", event.getNumberOfDays());
        log.info("Leave status: {}", event.getLeaveStatus());
        log.info("Leave reason: {}", event.getReason());
        log.info("Leave rejection reason: {}", event.getRejectionReason());
        log.info("Leave applied date: {}", event.getAppliedDate());
        log.info("Timestamp: {}", event.getTimestamp());
        log.info("Message: {}", event.getMessage());
        log.info("===============================================================");
    }
}
