package com.microservices.leaveservice.dto;

import jakarta.websocket.server.ServerEndpoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerLeaveResponse
{
    private Long leaveId;

    private Long employeeId;
    private String employeeName;
    private String employeeEmail;

    private String leaveType;
    private String status;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate appliedDate;

    private Integer numberOfDays;
    private String reason;
    private String rejectionReason;
}
