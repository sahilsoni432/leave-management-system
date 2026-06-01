package com.microservices.employeeservice.repository;

import com.microservices.employeeservice.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>
{
    Optional<Employee> findByUserId(Long userId);
}
