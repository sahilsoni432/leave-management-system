package com.microservices.leaveservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceResponse
{
    private Long employeeId;

    private int casualAllocated;
    private int casualUsed;
    private int casualRemaining;

    private int sickAllocated;
    private int sickUsed;
    private int sickRemaining;

    private int privilegeAllocated;
    private int privilegeUsed;
    private int privilegeRemaining;
}
