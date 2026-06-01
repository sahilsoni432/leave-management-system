package com.microservices.leaveservice.repository;

import com.microservices.leaveservice.entity.LeaveRequest;
import com.microservices.leaveservice.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>
{
    boolean existsByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long employeeId,
            LocalDate endDate,
            LocalDate startDate
    );

    List<LeaveRequest> findByManagerId(Long managerId);

    List<LeaveRequest> findByEmployeeId(Long employeeId);
}
