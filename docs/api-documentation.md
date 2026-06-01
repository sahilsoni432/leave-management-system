# Leave Management System API Documentation
 
## Overview
 
The Leave Management System is built using Spring Boot Microservices architecture.
 
The system consists of the following services:
 
- API Gateway
- Auth Service
- Employee Service
- Leave Service
- Notification Service
 
All requests are routed through API Gateway and secured using JWT authentication.
 
---
 
# Auth Service APIs
 
Base URL:
 
```http
/auth
```
 
## Login User
 
Authenticates user and returns JWT token.
 
### Endpoint
 
```http
POST /auth/login
```
 
### Request Body
 
```json
{
  "email": "john.doe@company.com",
  "password": "password123"
}
```
 
### Response
 
```json
{
  "token": "jwt-token",
  "employeeId": 1,
  "role": "ROLE_EMPLOYEE"
}
```
 
---
 
## Check User Existence (Internal API)
 
Used internally by Employee Service.
 
### Endpoint
 
```http
GET /auth/users/{userId}/exists
```
 
### Response
 
```json
true
```
 
---
 
# Employee Service APIs
 
Base URL:
 
```http
/employees
```
 
## Create Employee
 
Creates a new employee profile.
 
### Endpoint
 
```http
POST /employees/create
```
 
### Request Body
 
```json
{
  "name": "John Doe",
  "email": "john.doe@company.com",
  "password": "password123",
  "role": "ROLE_EMPLOYEE",
  "managerId": 2
}
```
 
### Response
 
```json
{
  "employeeId": 1,
  "name": "John Doe",
  "email": "john.doe@company.com"
}
```
 
---
 
## Get My Leave Balance
 
Returns leave balance for logged-in employee.
 
### Endpoint
 
```http
GET /employees/my-leave-balance
```
 
### Authorization
 
```text
ROLE_EMPLOYEE
```
 
### Response
 
```json
{
  "casualLeaves": 10,
  "sickLeaves": 8,
  "remainingLeaves": 18
}
```
 
---
 
## Get Employee Leave Balance (Internal API)
 
Used internally by Leave Service.
 
### Endpoint
 
```http
GET /employees/{employeeId}/leave-balance
```
 
### Response
 
```json
{
  "casualLeaves": 10,
  "sickLeaves": 8,
  "remainingLeaves": 18
}
```
 
---
 
## Deduct Employee Leave (Internal API)
 
Used internally by Leave Service after leave approval.
 
### Endpoint
 
```http
PUT /employees/deduct-leave
```
 
### Request Body
 
```json
{
  "employeeId": 1,
  "leaveType": "CASUAL",
  "numberOfDays": 2
}
```
 
### Response
 
```json
true
```
 
---
 
## Get Employee Authentication Details (Internal API)
 
Used internally by Auth Service.
 
### Endpoint
 
```http
GET /employees/auth/{userId}
```
 
### Response
 
```json
{
  "employeeId": 1,
  "email": "john.doe@company.com",
  "password": "encrypted-password",
  "role": "ROLE_EMPLOYEE"
}
```
 
---
 
## Get Employee Details (Internal API)
 
Used internally by Leave Service while processing leave requests and notifications.
 
### Endpoint
 
```http
GET /employees/{employeeId}
```
 
### Response
 
```json
{
  "employeeId": 1,
  "employeeName": "John Doe",
  "email": "john.doe@company.com",
  "managerId": 2,
  "role": "ROLE_EMPLOYEE"
}
```
 
---
 
# Leave Service APIs
 
Base URL:
 
```http
/leaves
```
 
## Apply Leave
 
Employee applies for leave.
 
### Endpoint
 
```http
POST /leaves/apply
```
 
### Authorization
 
```text
ROLE_EMPLOYEE
```
 
### Request Body
 
```json
{
  "leaveType": "CASUAL",
  "startDate": "2026-06-01",
  "endDate": "2026-06-03",
  "reason": "Personal Work"
}
```
 
### Response
 
```json
{
  "leaveId": 101,
  "status": "PENDING"
}
```
 
---
 
## Get Manager Leave Requests
 
Returns all pending leave requests assigned to manager.
 
### Endpoint
 
```http
GET /leaves/manager
```
 
### Authorization
 
```text
ROLE_MANAGER
```
 
---
 
## Approve Leave
 
Manager approves employee leave request.
 
### Endpoint
 
```http
PUT /leaves/{leaveId}/approve
```
 
### Authorization
 
```text
ROLE_MANAGER
```
 
### Response
 
```json
{
  "leaveId": 101,
  "status": "APPROVED"
}
```
 
---
 
## Reject Leave
 
Manager rejects employee leave request.
 
### Endpoint
 
```http
PUT /leaves/{leaveId}/reject?reason=Insufficient%20Resources
```
 
### Authorization
 
```text
ROLE_MANAGER
```
 
### Response
 
```json
{
  "leaveId": 101,
  "status": "REJECTED"
}
```
 
---
 
## Get My Leave History
 
Returns leave history of logged-in employee.
 
### Endpoint
 
```http
GET /leaves/my-leave-history
```
 
### Authorization
 
```text
ROLE_EMPLOYEE
```
 
### Response
 
```json
{
  "content": [
    {
      "leaveId": 101,
      "status": "APPROVED"
    }
  ]
}
```
 
---
 
# Notification Service
 
Notification Service does not expose any REST APIs.
 
It consumes events asynchronously from RabbitMQ queue.
 
Supported events:
 
- Leave Applied
- Leave Approved
- Leave Rejected
 
---
 
# Security
 
Authentication mechanism:
 
- JWT Token Based Authentication
 
Authorization Roles:
 
```text
ROLE_EMPLOYEE
ROLE_MANAGER
```
 
JWT token must be passed in Authorization header.
 
Example:
 
```http
Authorization: Bearer <jwt-token>
```
 
---
 
# Service Communication
 
## Synchronous Communication (Feign Clients)
 
### Auth Service → Employee Service
 
```text
GET /employees/auth/{userId}
```
 
Used during login authentication.
 
### Leave Service → Employee Service
 
```text
GET /employees/{employeeId}
GET /employees/{employeeId}/leave-balance
PUT /employees/deduct-leave
```
 
Used during leave validation and approval workflow.
 
---
 
## Asynchronous Communication (RabbitMQ)
 
### Producer
 
```text
Leave Service
```
 
### Exchange
 
```text
leave-notification-exchange
```
 
### Queue
 
```text
leave-notification-queue
```
 
### Routing Key
 
```text
leave.notification
```
 
### Consumer
 
```text
Notification Service
```
 
### Events Published
 
```text
LEAVE_APPLIED
LEAVE_APPROVED
LEAVE_REJECTED
```
 