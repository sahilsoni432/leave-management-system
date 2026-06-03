package com.microservices.employeeservice.config;

import com.microservices.employeeservice.entity.Employee;
import com.microservices.employeeservice.entity.LeaveBalance;
import com.microservices.employeeservice.enums.Role;
import com.microservices.employeeservice.repository.EmployeeRepository;
import com.microservices.employeeservice.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeDataInitializer implements CommandLineRunner
{
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public void run(String... args)
    {

        if (employeeRepository.count() == 0) {

            Employee manager = new Employee();
            manager.setUserId(1L);
            manager.setName("Test Manager");
            manager.setEmail("manager@test.com");
            manager.setRole(Role.ROLE_MANAGER);
            manager.setManagerId(null);

            Employee employee = new Employee();
            employee.setUserId(2L);
            employee.setName("Test Employee");
            employee.setEmail("employee@test.com");
            employee.setRole(Role.ROLE_EMPLOYEE);
            employee.setManagerId(1L);

            employeeRepository.save(manager);
            employeeRepository.save(employee);

            createLeaveBalance(employee.getId());

            log.info("Default employees inserted successfully.");
        }
    }

    private void createLeaveBalance(Long employeeId)
    {
        Optional<Employee> employee = employeeRepository.findById(employeeId);

        if(employee.isPresent() && Role.ROLE_EMPLOYEE.equals(employee.get().getRole())
                && leaveBalanceRepository.findByEmployeeId(employeeId).isEmpty())
        {
            LeaveBalance leaveBalance = new LeaveBalance();
            leaveBalance.setEmployeeId(employeeId);

            leaveBalance.setCasualAllocated(12);
            leaveBalance.setCasualUsed(0);

            leaveBalance.setSickAllocated(10);
            leaveBalance.setSickUsed(0);

            leaveBalance.setPrivilegeAllocated(15);
            leaveBalance.setPrivilegeUsed(0);

            leaveBalanceRepository.save(leaveBalance);

            log.info("Auto leave allocation completed for employeeId: {}", employee);
        }
    }
}
