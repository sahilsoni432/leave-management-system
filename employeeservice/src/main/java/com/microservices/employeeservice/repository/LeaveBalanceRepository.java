package com.microservices.employeeservice.repository;

import com.microservices.employeeservice.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long>
{
    Optional<LeaveBalance> findByEmployeeId(Long employeeId);
}
