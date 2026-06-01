package com.microservices.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent
{
    private String eventType;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String employeeRole;
    private Long managerId;
    private Long leaveId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfDays;
    private String leaveStatus;
    private String reason;
    private String rejectionReason;
    private LocalDate appliedDate;
    private String message;
    private LocalDateTime timestamp;
}
