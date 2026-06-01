package com.microservices.employeeservice.exception;

public class AuthServiceUnavailableException extends RuntimeException
{
    public AuthServiceUnavailableException(String message)
    {
        super(message);
    }
}
