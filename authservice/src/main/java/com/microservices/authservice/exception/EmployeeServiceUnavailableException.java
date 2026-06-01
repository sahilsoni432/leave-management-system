package com.microservices.authservice.exception;

public class EmployeeServiceUnavailableException extends RuntimeException
{
    public EmployeeServiceUnavailableException(String message)
    {
        super(message);
    }
}
