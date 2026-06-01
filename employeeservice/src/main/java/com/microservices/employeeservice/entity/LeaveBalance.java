package com.microservices.employeeservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="leave_balance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long employeeId;


    private int casualAllocated;
    private int casualUsed;

    private int sickAllocated;
    private int sickUsed;

    private int privilegeAllocated;
    private int privilegeUsed;
}
