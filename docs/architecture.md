# Leave Management System Architecture
 
## Overview
 
Leave Management System is a Spring Boot based Microservices application designed to manage employee leave operations, authentication, authorization, service communication and fault tolerance.
 
The system follows a distributed microservices architecture where each service has a dedicated responsibility and communicates with other services using OpenFeign clients and service discovery through Eureka Server.
 
All incoming requests are routed through API Gateway which acts as a centralized entry point for authentication, request validation and routing.
 
---
 
## High Level Architecture
  +----------------+
                              |     Client     |
                              +----------------+
                                       |
                                       v
                              +----------------+
                              |  API Gateway   |
                              +----------------+
                                       |
      -----------------------------------------------------------------
      |                       |                      |                 |
      v                       v                      v                 v
 
+---------------+    +----------------+    +---------------+   +----------------------+
| Auth Service  |    | EmployeeService|    | Leave Service |   | Notification Service |
+---------------+    +----------------+    +---------------+   +----------------------+
 
 
                         Service Discovery (Eureka)
 
+---------------+         +----------------+         +---------------+
| Auth Service  |-------->|                |<--------| Leave Service |
+---------------+         |                |         +---------------+
                          | Eureka Server  |
+----------------+------->|                |<--------+----------------------+
| EmployeeService|        |                |         | Notification Service |
+----------------+        +----------------+         +----------------------+
 
                                   ^
                                   |
                                   |
                           +---------------+
                           | API Gateway   |
                           +---------------+
 
 
                    Synchronous Communication (Feign)
 
                     Check if user exists
+---------------+ <--------------------------- +----------------+
| Auth Service  | ---------------------------> | EmployeeService|
+---------------+   Get Employee Auth Details  +----------------+
 
 
+---------------+                              +----------------+
| Leave Service | ---------------------------> | EmployeeService|
+---------------+     Employee Data Lookup     +----------------+
 
 
 
                  Asynchronous Communication (RabbitMQ)
 
+---------------+        Publish Event         +-------------+
| Leave Service | ---------------------------> |  RabbitMQ   |
+---------------+                              +-------------+
                                                      |
                                                      |
                                                      | Consume Event
                                                      v
                                         +----------------------+
                                         | Notification Service |
                                         +----------------------+

                  Centralized Logging & Monitoring (ELK Stack)
 
+------------------+      Logs      +-----------+
| API Gateway      | -------------> |           |
| Auth Service     | -------------> |           |
| Employee Service | -------------> | Filebeat  |
| Leave Service    | -------------> |           |
| NotificationSvc  | -------------> |           |
+------------------+                +-----------+
                                           |
                                           v
                                    +-------------+
                                    | Elasticsearch|
                                    +-------------+
                                           |
                                           v
                                      +---------+
                                      | Kibana  |
                                      +---------+
 
Log Flow:
Microservices → Filebeat → Elasticsearch → Kibana
 
Events Published:
- Leave Applied
- Leave Approved
- Leave Rejected
```
 
All microservices register themselves with Eureka Server.
API Gateway routes requests using service discovery.
Employee Service is invoked by Leave Service using OpenFeign.
Notification events are published to RabbitMQ and consumed asynchronously by Notification Service.
 
## System Components
 
### 1. Eureka Server
 
Eureka Server acts as the Service Registry of the system.
 
Responsibilities:
 
- Service Registration
- Service Discovery
- Dynamic Service Lookup
- Load Balancing Support
 
Every microservice registers itself with Eureka during startup and becomes discoverable by other services.
 
Benefits:
 
- Eliminates hardcoded service URLs
- Enables dynamic scaling
- Simplifies inter-service communication
 
---
 
### 2. API Gateway
 
API Gateway serves as the single entry point for all incoming client requests.
 
Responsibilities:
 
- Request Routing
- JWT Validation
- Authentication
- Authorization
- Header Propagation
- Request Filtering
 
All external requests pass through the gateway before reaching backend services.
 
The gateway validates incoming JWT tokens and extracts user information required by downstream services.
 
---
 
### 3. Auth Service
 
Auth Service is responsible for authentication and JWT token generation.
 
Responsibilities:
 
- User Authentication
- JWT Generation
- JWT Claim Creation
- Employee Authorization Lookup
 
During login, Auth Service fetches employee details from Employee Service and generates a signed JWT token containing user information.
 
---
 
### 4. Employee Service
 
Employee Service manages employee information and leave balances.
 
Responsibilities:
 
- Employee Creation
- Employee Information Management
- Leave Balance Management
- Role Validation
- Employee Authorization Information
 
The service also exposes internal APIs consumed by Auth Service.
 
---
 
### 5. Leave Service
 
Leave Service handles leave related business operations.
 
Responsibilities:
 
- Leave Request Processing
- Leave Approval Workflow
- Leave Status Management
- Leave Tracking
 
This service can be independently scaled based on leave processing load.
 
---
 
### 6. Notification Service
 
Notification Service handles notification related functionality.
 
Responsibilities:
 
- Email Notifications
- Leave Status Notifications
- Employee Related Notifications
- System Event Notifications
 
Keeping notifications in a dedicated service improves scalability and separation of concerns.
 
---
 
## Authentication Flow
 
Authentication is handled by Auth Service using JWT (JSON Web Token).
 
### Login Flow
 
```text
User
  |
  v
Auth Service
  |
  | Feign Call
  v
Employee Service
  |
  v
EmployeeAuthResponse
  |
  v
JWT Generation
  |
  v
JWT Token Returned
```
 
### Login Steps
 
1. User sends login request.
2. Auth Service validates credentials.
3. Auth Service calls Employee Service using Feign Client.
4. Employee Service returns employee details.
5. JWT token is generated.
6. Token is returned to the client.
 
---
 
## JWT Architecture
 
JWT token contains business claims required by downstream services.
 
### JWT Claims
 
```json
{
  "userId": 101,
  "employeeId": 15,
  "role": "ROLE_MANAGER"
}
```
 
### JWT Signing
 
Tokens are digitally signed using a secret key.
 
```java
Keys.hmacShaKeyFor(secretKey.getBytes())
```
 
### JWT Validation
 
Gateway validates every incoming token before forwarding requests.
 
```java
Jwts.parser()
    .verifyWith(getSignKey())
    .build()
    .parseSignedClaims(token);
```
 
---
 
## Request Processing Flow
 
Every incoming request follows the flow below:
 
```text
Client
   |
   v
API Gateway
   |
   | Validate JWT
   |
   | Extract Claims
   |
   | Add Internal Headers
   |
   v
Microservice
```
 
### Extracted Claims
 
Gateway extracts:
 
- userId
- employeeId
- role
 
### Internal Headers Added
 
```text
X-User-Id
X-Employee-Id
X-Role
```
 
These headers are forwarded to downstream services and used for authorization and business validations.
 
---
 
## Authorization Model
 
The system uses Role Based Access Control (RBAC).
 
### Supported Roles
 
```text
ROLE_EMPLOYEE
ROLE_MANAGER
```
 
### Employee Permissions
 
- View own profile
- View own leave balance
- Submit leave requests
 
### Manager Permissions
 
- Approve leaves
- Reject leaves
- Deduct leaves
- Manage employees
 
---
 
## Leave Management
 
Leave allocation is configured centrally.
 
### Default Leave Allocation
 
```yaml
leave:
  allocation:
    casual: 12
    sick: 10
    privilege: 15
```
 
### Supported Leave Types
 
- CASUAL
- SICK
- PRIVILEGE
 
Employee leave balances are maintained and updated by Employee Service.
 
---
 
## Inter-Service Communication
 
Microservices communicate using OpenFeign clients.
 
Example:
 
```java
@FeignClient(name = "EMPLOYEESERVICE")
```
 
Auth Service communicates with Employee Service to retrieve employee information required for token generation.
 
Benefits:
 
- Cleaner Code
- Reduced Boilerplate
- Service Discovery Integration
- Better Maintainability
 
---
 
## Service Discovery
 
Service Discovery is implemented using Eureka Server.
 
Instead of using hardcoded URLs, services communicate using logical service names.
 
Example:
 
```java
@FeignClient(name = "EMPLOYEESERVICE")
```
 
Eureka resolves the actual service instance dynamically.
 
Benefits:
 
- Dynamic Service Lookup
- Easier Scaling
- Improved Maintainability
 
---
 
## Fault Tolerance
 
The system uses Resilience4j to improve reliability.
 
### Retry
 
Retry automatically retries failed service calls.
 
Configuration:
 
```yaml
max-attempts: 3
wait-duration: 500ms
```
 
Flow:
 
```text
Attempt 1
   |
Failure
   |
Attempt 2
   |
Failure
   |
Attempt 3
```
 
---
 
### Circuit Breaker
 
Circuit Breaker prevents cascading failures.
 
Configuration:
 
```yaml
failure-rate-threshold: 50
minimum-number-of-calls: 5
sliding-window-size: 10
wait-duration-in-open-state: 10s
```
 
Circuit Breaker States:
 
```text
CLOSED
OPEN
HALF_OPEN
```
 
Benefits:
 
- Prevents repeated failures
- Protects dependent services
- Improves overall system stability
 
---
 
## Distributed Tracing
 
The system uses Zipkin for distributed tracing.
 
Configuration:
 
```yaml
management:
  zipkin:
    tracing:
      endpoint:
        http://zipkin:9411/api/v2/spans
```
 
### Trace Flow
 
```text
API Gateway
      |
      v
Auth Service
      |
      v
Employee Service
```
 
A unique Trace ID follows the request across all services.
 
Benefits:
 
- Easier Debugging
- Faster Root Cause Analysis
- Better Monitoring
 
---
 
## Logging Strategy
 
Logs contain tracing information.
 
Example:
 
```text
traceId
spanId
```
 
Benefits:
 
- Request Tracking
- Easier Production Debugging
- Service Correlation
 
---
 
## Database
 
MySQL is used as the primary database.
 
Persistence Layer:
 
- Spring Data JPA
- Hibernate ORM
 
Schema Management:
 
```yaml
ddl-auto: update
```
 
Benefits:
 
- Automatic Schema Updates
- Simplified ORM Mapping
- Reduced Database Boilerplate
 
---
 
## Technology Stack
 
### Backend
 
- Java 17
- Spring Boot
- Spring Security
- Spring Cloud Gateway
- Spring Cloud OpenFeign
 
### Service Discovery
 
- Eureka Server
 
### Security
 
- JWT Authentication
- Role Based Access Control
 
### Database
 
- MySQL
- Hibernate
- Spring Data JPA
 
### Reliability
 
- Resilience4j Retry
- Resilience4j Circuit Breaker
 
### Observability
 
- Zipkin
- Distributed Tracing
- Elasticsearch
- Kibana
- Filebeat
- Centralized Logging
 
### Build & Deployment
 
- Maven
- Docker
- Docker Compose
- Docker Hub Images
- ELK Stack Containers
  - Elasticsearch
  - Kibana
  - Filebeat
 
---
 
## Logging & Monitoring
 
The system uses ELK Stack for centralized logging and monitoring.
 
Components:
 
### Filebeat
Collects logs from all microservices and forwards them to Elasticsearch.
 
### Elasticsearch
Stores and indexes application logs for efficient searching and analysis.
 
### Kibana
Provides visualization dashboards and log exploration capabilities.
 
Benefits:
 
- Centralized log management
- Faster debugging
- Production monitoring
- Log searching and filtering
- Service health analysis

## Conclusion
 
The Leave Management System follows a scalable and maintainable microservices architecture. Authentication is centralized through Auth Service, request validation is handled by API Gateway, service communication is implemented using OpenFeign and Eureka, while resilience and observability are achieved using Resilience4j, Zipkin and ELK Stack (Elasticsearch, Kibana, Filebeat).
 
This architecture enables independent deployment, scalability, fault tolerance, secure communication between services and centralized log monitoring.
 