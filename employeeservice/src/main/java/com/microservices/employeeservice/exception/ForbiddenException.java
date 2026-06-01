package com.microservices.employeeservice.exception;

public class ForbiddenException extends RuntimeException
{
    public ForbiddenException(String message)
    {
        super(message);
    }
}
