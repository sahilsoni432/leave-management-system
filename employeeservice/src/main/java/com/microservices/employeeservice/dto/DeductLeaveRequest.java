package com.microservices.employeeservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeductLeaveRequest
{
    @NotNull(message = "Employee Id is required")
    private Long employeeId;

    @NotBlank(message = "Leave type is required")
    private String leaveType;

    @Min(value = 1, message = "Days must be at least 1")
    private int numberOfDays;
}
