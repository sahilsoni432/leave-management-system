package com.microservices.leaveservice.entity;

import com.microservices.leaveservice.enums.LeaveStatus;
import com.microservices.leaveservice.enums.LeaveType;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long employeeId;

    @NotNull
    @Column(nullable = false)
    private Long managerId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private LeaveType leaveType;

    @NotNull
    @FutureOrPresent
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(nullable = false)
    private int numberOfDays;

    @NotNull
    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private LeaveStatus status;

    @NotNull
    @Column(nullable = false)
    private LocalDate appliedDate;

    private String rejectionReason;
}
