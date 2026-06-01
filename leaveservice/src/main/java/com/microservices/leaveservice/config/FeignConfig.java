package com.microservices.leaveservice.config;

import com.microservices.leaveservice.constants.ApplicationConstants;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig
{
    @Bean
    public RequestInterceptor requestInterceptor()
    {
        return requestTemplate -> {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if(attributes != null)
                {
                    HttpServletRequest request = attributes.getRequest();
                    String role = request.getHeader(ApplicationConstants.X_ROLE);
                    String userId = request.getHeader(ApplicationConstants.X_USER_ID);
                    String employeeId = request.getHeader(ApplicationConstants.X_EMPLOYEE_ID);

                    if(role != null)
                    {
                        requestTemplate.header(ApplicationConstants.X_ROLE, role);
                    }
                    if(userId != null)
                    {
                        requestTemplate.header(ApplicationConstants.X_USER_ID, userId);
                    }
                    if(employeeId != null)
                    {
                        requestTemplate.header(ApplicationConstants.X_EMPLOYEE_ID, employeeId);
                    }
                }
        };
    }
}
