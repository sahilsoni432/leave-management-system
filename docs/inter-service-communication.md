# Inter-Service Communication
 
## Overview
 
The Leave Management System follows a microservices architecture where services communicate using both synchronous and asynchronous communication patterns.
 
### Communication Types
 
1. Synchronous Communication
   - REST APIs
   - OpenFeign Clients
   - Service Discovery through Eureka
 
2. Asynchronous Communication
   - RabbitMQ Message Broker
   - Event Driven Notifications
 
---
 
# Service Discovery using Eureka
 
All microservices register themselves with Eureka Server during startup.
 
Registered Services:
 
- API Gateway
- Auth Service
- EmployeeService
- Leave Service
- Notification Service
 
Benefits:
 
- Dynamic service discovery
- No hardcoded service URLs
- Load balancing support
- Fault tolerance
 
Example:
 
Instead of calling:
 
http://localhost:8082/employees/1
 
Services use:
 
EMPLOYEESERVICE
 
and Eureka resolves the actual instance automatically.
 
---
 
# API Gateway Communication
 
All external requests first pass through API Gateway.
 
Responsibilities:
 
- Route requests to target services
- Validate JWT token
- Extract user information from JWT
- Forward authenticated user headers
 
Request Flow:
 
Client
    |
    v
API Gateway
    |
    v
Target Service
 
---
 
# JWT Validation Flow
 
JWT validation is performed inside API Gateway.
 
Steps:
 
1. Extract Authorization header.
2. Validate JWT signature.
3. Extract claims from token:
   - userId
   - role
   - employeeId
4. Add headers to outgoing request.
5. Forward request to downstream service.
 
Headers forwarded:
 
X-User-Id
X-Role
X-Employee-Id
 
Example:
 
Authorization: Bearer eyJhbGci...
 
After validation:
 
X-User-Id: 101
X-Role: EMPLOYEE
X-Employee-Id: 1001
 
This eliminates the need for every service to validate JWT independently.
 
---
 
# Auth Service → EmployeeService Communication
 
## Communication Type
 
Synchronous
 
## Technology
 
OpenFeign Client
 
## Purpose
 
During login, Auth Service fetches employee authentication details from EmployeeService.
 
Feign Client:
 
```java
@FeignClient(name = "EMPLOYEESERVICE")
public interface EmployeeServiceClient
```
 
Endpoint Invoked:
 
```text
GET /employees/auth/{userId}
```
 
Flow:
 
Client Login Request
        |
        v
Auth Service
        |
        v
EmployeeService
        |
        v
Employee Authentication Data
        |
        v
JWT Generation
 
Returned Information:
 
- User Id
- Employee Id
- Password
- Role
 
Auth Service then generates JWT token.
 
---
 
# Leave Service → EmployeeService Communication
 
## Communication Type
 
Synchronous
 
## Technology
 
OpenFeign Client
 
Feign Client:
 
```java
@FeignClient(name = "EMPLOYEESERVICE")
public interface EmployeeServiceClient
```
 
Used APIs:
 
```text
GET /employees/{employeeId}
```
 
```text
GET /employees/{employeeId}/leave-balance
```
 
```text
PUT /employees/deduct-leave
```
 
Purpose:
 
### 1. Employee Data Lookup
 
Leave Service fetches employee details.
 
Used For:
 
- Employee validation
- Manager information retrieval
- Notification data generation
 
---
 
### 2. Leave Balance Validation
 
Before leave application:
 
Leave Service checks:
 
- Casual Leave Balance
- Sick Leave Balance
- Earned Leave Balance
 
If sufficient balance exists:
 
Leave application proceeds.
 
Otherwise:
 
Request is rejected.
 
---
 
### 3. Leave Deduction
 
After successful leave approval:
 
Leave Service requests EmployeeService to deduct leave balance.
 
Flow:
 
Leave Service
        |
        v
EmployeeService
        |
        v
Leave Balance Updated
 
---
 
# RabbitMQ Communication
 
## Communication Type
 
Asynchronous
 
## Producer
 
Leave Service
 
## Consumer
 
Notification Service
 
---
 
# RabbitMQ Configuration
 
Queue:
 
```text
leave-notification-queue
```
 
Exchange:
 
```text
leave-notification-exchange
```
 
Routing Key:
 
```text
leave.notification
```
 
Architecture:
 
Leave Service
      |
      v
RabbitMQ Exchange
      |
      v
RabbitMQ Queue
      |
      v
Notification Service
 
---
 
# Notification Event Publishing
 
Leave Service publishes notification events whenever a leave action occurs.
 
Published Events:
 
- LEAVE_APPLIED
- LEAVE_APPROVED
- LEAVE_REJECTED
 
Flow:
 
Employee Applies Leave
          |
          v
Leave Service
          |
          v
Notification Event Created
          |
          v
RabbitMQ Queue
 
Notification event contains:
 
- Employee Id
- Employee Name
- Employee Email
- Employee Role
- Manager Id
- Leave Id
- Start Date
- End Date
- Number Of Days
- Leave Status
- Reason
- Rejection Reason
- Applied Date
- Timestamp
 
---
 
# Notification Service Consumption
 
Notification Service listens continuously on RabbitMQ queue.
 
Consumer:
 
```java
@RabbitListener(
    queues = RabbitMQConfig.QUEUE
)
```
 
Process:
 
RabbitMQ Queue
        |
        v
Notification Consumer
        |
        v
Notification Service
        |
        v
Email Notification
 
---
 
# Complete Request Flow
 
## Login Flow
 
Client
    |
    v
API Gateway
    |
    v
Auth Service
    |
    v
EmployeeService
    |
    v
JWT Generated
    |
    v
Client
 
---
 
## Apply Leave Flow
 
Client
    |
    v
API Gateway
    |
    v
Leave Service
    |
    v
EmployeeService
(Check Leave Balance)
    |
    v
Leave Created
    |
    v
RabbitMQ
    |
    v
Notification Service
    |
    v
Email Sent
 
---
 
## Approve Leave Flow
 
Manager
    |
    v
API Gateway
    |
    v
Leave Service
    |
    v
EmployeeService
(Deduct Leave Balance)
    |
    v
RabbitMQ
    |
    v
Notification Service
    |
    v
Approval Email Sent
 
---
 
## Reject Leave Flow
 
Manager
    |
    v
API Gateway
    |
    v
Leave Service
    |
    v
RabbitMQ
    |
    v
Notification Service
    |
    v
Rejection Email Sent
 
---
 
# Summary
 
Synchronous Communication
 
- Auth Service → EmployeeService
- Leave Service → EmployeeService
 
Asynchronous Communication
 
- Leave Service → RabbitMQ → Notification Service
 
Service Discovery
 
- Eureka Server
 
Authentication
 
- JWT Validation in API Gateway
 
This architecture ensures loose coupling, scalability, and independent deployment of services.
 