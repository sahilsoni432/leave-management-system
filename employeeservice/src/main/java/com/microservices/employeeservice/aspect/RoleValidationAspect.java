package com.microservices.employeeservice.aspect;

import com.microservices.employeeservice.annotations.RoleAllowed;
import com.microservices.employeeservice.constants.ApplicationConstants;
import com.microservices.employeeservice.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RoleValidationAspect
{
    @Autowired
    private HttpServletRequest request;

    @Before("@annotation(roleAllowed)")
    public void validateRole(JoinPoint joinPoint, RoleAllowed roleAllowed)
    {
        log.info("Validating role access");
        String role = request.getHeader(ApplicationConstants.X_ROLE);
        log.info("Role received in header: {}", role);
        if(role == null || !role.equals(roleAllowed.value().name()))
        {
            log.error("Access denied for role: {}", role);
            throw new ForbiddenException("Access denied");
        }
    }
}
