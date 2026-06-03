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
  "email": "employee@test.com",
  "password": "password123"
}
```

```json
{
  "email": "manager@test.com",
  "password": "password123"
}
```
 
### Response
 
```json
{
  "token": "jwt-token",
  "role": "ROLE_EMPLOYEE",
  "userId": 2
}
```

```json
{
  "token": "jwt-token",
  "role": "ROLE_MANAGER",
  "userId": 1
}
```

### Decoded JWT response  
 
```json
{
  "userId": 2,
  "role": "ROLE_EMPLOYEE",
  "employeeId": 2,
  "iat": 1780507007,
  "exp": 1780593407
}
```

```json
{
  "userId": 1,
  "role": "ROLE_MANAGER",
  "employeeId": 1,
  "iat": 1780508130,
  "exp": 1780594530
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
  "userId": "101",
  "role": "ROLE_EMPLOYEE",
  "managerId": 1
}
```
 
### Response
 
```json
{
  "id": 1,
  "userId": "101",
  "name": "John Doe",
  "email": "john.doe@company.com",
  "role": "ROLE_EMPLOYEE",
  "managerId": 1
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
    "employeeId": 2,
    "casualAllocated": 12,
    "casualUsed": 0,
    "casualRemaining": 12,
    "sickAllocated": 10,
    "sickUsed": 0,
    "sickRemaining": 10,
    "privilegeAllocated": 15,
    "privilegeUsed": 0,
    "privilegeRemaining": 15
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
    "employeeId": 2,
    "casualAllocated": 12,
    "casualUsed": 0,
    "casualRemaining": 12,
    "sickAllocated": 10,
    "sickUsed": 0,
    "sickRemaining": 10,
    "privilegeAllocated": 15,
    "privilegeUsed": 0,
    "privilegeRemaining": 15
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
  "employeeId": 2,
  "userId": 2,
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
  "employeeEmail": "john.doe@company.com",
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
    "managerId": 1,
    "leaveType": "CASUAL",
    "startDate": "2026-06-20",
    "endDate": "2026-06-21",
    "numberOfDays": 3,
    "reason": "Family Function"
}
```
 
### Response
 
```json
{
    "id": 1,
    "employeeId": 2,
    "managerId": 1,
    "leaveType": "CASUAL",
    "startDate": "2026-06-20",
    "endDate": "2026-06-21",
    "numberOfDays": 3,
    "reason": "Family Function",
    "status": "PENDING",
    "appliedDate": "2026-06-03",
    "rejectionReason": null
}
```
 
---
 
## Get Manager Leave Requests
 
Returns all pending leave requests assigned to manager.
 
### Endpoint
 
```http
GET /leaves/manager?status=ALL&employeeId=2&fromDate=2026-06-01&toDate=2026-06-30
```
 
### Authorization
 
```text
ROLE_MANAGER
```
 
---

### Response
 
```json
[
    {
        "leaveId": 1,
        "employeeId": 2,
        "employeeName": "Test Employee",
        "employeeEmail": "employee@test.com",
        "leaveType": "CASUAL",
        "status": "PENDING",
        "startDate": "2026-06-20",
        "endDate": "2026-06-21",
        "appliedDate": "2026-06-03",
        "numberOfDays": 3,
        "reason": "Family Function",
        "rejectionReason": null
    }
]
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
    "id": 1,
    "employeeId": 2,
    "managerId": 1,
    "leaveType": "CASUAL",
    "startDate": "2026-06-20",
    "endDate": "2026-06-21",
    "numberOfDays": 3,
    "reason": "Family Function",
    "status": "APPROVED",
    "appliedDate": "2026-06-03",
    "rejectionReason": null
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
    "id": 2,
    "employeeId": 2,
    "managerId": 1,
    "leaveType": "SICK",
    "startDate": "2026-06-22",
    "endDate": "2026-06-23",
    "numberOfDays": 1,
    "reason": "Suffering from fever",
    "status": "REJECTED",
    "appliedDate": "2026-06-03",
    "rejectionReason": "Insufficient"
}
```
 
---
 
## Get My Leave History
 
Returns leave history of logged-in employee.
 
### Endpoint
 
```http
GET /leaves/my-leave-history?status=ALL&leaveType=ALL&fromDate=2026-06-01&toDate=2026-06-30&page=0&size=10
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
            "id": 1,
            "employeeId": 2,
            "managerId": 1,
            "leaveType": "CASUAL",
            "startDate": "2026-06-20",
            "endDate": "2026-06-21",
            "numberOfDays": 3,
            "reason": "Family Function",
            "status": "APPROVED",
            "appliedDate": "2026-06-03",
            "rejectionReason": null
        },
        {
            "id": 2,
            "employeeId": 2,
            "managerId": 1,
            "leaveType": "SICK",
            "startDate": "2026-06-22",
            "endDate": "2026-06-23",
            "numberOfDays": 1,
            "reason": "Suffering from fever",
            "status": "REJECTED",
            "appliedDate": "2026-06-03",
            "rejectionReason": "\"May be next time\""
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": [],
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 2,
    "last": true,
    "first": true,
    "numberOfElements": 2,
    "size": 10,
    "number": 0,
    "sort": [],
    "empty": false
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
 