package com.microservices.leaveservice.dto;

import com.microservices.leaveservice.enums.LeaveType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyLeaveRequest
{
    @NotNull(message = "ManagetId is required")
    private Long managerId;

    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Past dates are not allowed")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Number of Days is/are required")
    private int numberOfDays;

    @NotBlank(message = "Reason is required")
    private String reason;
}
