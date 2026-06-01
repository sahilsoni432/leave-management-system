package com.microservices.employeeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse
{
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String role;
    private Long managerId;
}
