package com.microservices.leaveservice.exception;

public class EmployeeServiceUnavailableException extends RuntimeException
{
    public EmployeeServiceUnavailableException(String message)
    {
        super(message);
    }
}
