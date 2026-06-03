## GitHub Repository
 
https://github.com/sahilsoni432/leave-management-system

# Leave Management System
 
A Spring Boot Microservices based Leave Management System implementing service discovery, API gateway, JWT authentication & authorization, distributed tracing, centralized logging, asynchronous communication, and containerized deployment using Docker.
 
---
 
## Documentation
 
- Architecture: `docs/architecture.md`
- API Documentation: `docs/api-documentation.md`
- Inter Service Communication: `docs/inter-service-communication.md`
- Docker Hub Images: `docs/docker-hub-images.md`
 
---
 
## Services
 
### Core Services
 
- API Gateway
- Auth Service
- Employee Service
- Leave Service
- Notification Service
- Eureka Server
 
### Infrastructure Services
 
- MySQL
- RabbitMQ
 
### Observability Stack
 
- Zipkin (Distributed Tracing)
- Elasticsearch (Log Storage)
- Filebeat (Log Collection)
- Kibana (Log Visualization)
 
---
 
## Architecture Highlights
 
- Spring Boot Microservices Architecture
- Spring Cloud Gateway
- Eureka Service Discovery
- JWT Based Authentication & Authorization
- Role Based Access Control (Employee / Manager)
- RabbitMQ Event Driven Communication
- Circuit Breaker using Resilience4j
- Distributed Tracing using Zipkin
- Centralized Logging using ELK Stack
- Dockerized Deployment
- Docker Hub Image Deployment
 
---
 
## Running Locally
  
### Start Application
 
```bash
docker compose up -d --build
```
 
### Stop Application
 
```bash
docker compose down
```
 
---
 
## Running Using Docker Hub Images
 
### Start Application
 
```bash
docker compose -f docker-compose-hub.yaml up -d
```
 
### Stop Application
 
```bash
docker compose -f docker-compose-hub.yaml down
```
 
---
 
## Environment Variables
 
### Database
 
| Variable | Description |
|-----------|------------|
| SPRING_DATASOURCE_URL | MySQL Connection URL |
| SPRING_DATASOURCE_USERNAME | Database Username |
| SPRING_DATASOURCE_PASSWORD | Database Password |
 
### Eureka
 
| Variable | Description |
|-----------|------------|
| EUREKA_CLIENT_SERVICEURL_DEFAULTZONE | Eureka Server URL |
 
 
### RabbitMQ
 
| Variable | Description |
|-----------|------------|
| SPRING_RABBITMQ_HOST | RabbitMQ Host |
| SPRING_RABBITMQ_PORT | RabbitMQ Port |
| SPRING_RABBITMQ_USERNAME | RabbitMQ Username |
| SPRING_RABBITMQ_PASSWORD | RabbitMQ Password |
 
---

---
 
## Default Test Data
 
The application automatically initializes default users, employees, and leave balances during startup if the required records are not already present in the database.
 
This allows the application to be tested immediately after deployment without requiring any manual database setup.
 
### Predefined Users
 
| User ID | Email | Password |
|----------|----------|----------|
| 1 | manager@test.com | password123 |
| 2 | employee@test.com | password123 |
 
### Predefined Employees
 
| Employee ID | User ID | Name | Role | Manager ID |
|------------|----------|-------------|---------------|------------|
| 1 | 1 | Test Manager | ROLE_MANAGER | N/A |
| 2 | 2 | Test Employee | ROLE_EMPLOYEE | 1 |
 
### Initial Leave Allocation
 
When employees are initialized, leave balances are automatically created.
 
| Employee ID | Casual Leave | Sick Leave | Privilege Leave |
|------------|-------------|------------|----------------|
| 1 | 12 | 10 | 15 |
| 2 | 12 | 10 | 15 |
 
### Login Credentials
 
#### Manager Login
 
```json
{
  "email": "manager@test.com",
  "password": "password123"
}
```
 
#### Employee Login
 
```json
{
  "email": "employee@test.com",
  "password": "password123"
}
```
 
### Notes
 
- Default users are created automatically during application startup.
- Default employees are created automatically during application startup.
- Leave balances are automatically allocated for initialized employees.
- Duplicate records are not created on subsequent application restarts.
- Manual database setup is not required for testing.
 
---
 
 
## API Testing Instructions
 
### 1. Start Application
 
```bash
docker compose up -d --build
```
 
### 2. Verify Eureka Registration
 
Open:
 
```
http://localhost:8761
```
 
Ensure all microservices are registered successfully.
 
### 3. Login and Generate JWT Token
 
Endpoint:
 
```
POST /api/auth/login
```
 
Sample Request:
 
```json
{
  "email": "employee@test.com",
  "password": "password123"
}
```
 
### 4. Copy JWT Token
 
Use the token returned by login API.
 
### 5. Pass JWT Token
 
Include JWT token in Authorization header:
 
```
Authorization: Bearer <JWT_TOKEN>
```
 
### 6. Test Employee APIs
 
- View Leave Balance
- Apply Leave
- View Leave History
 
### 7. Test Manager APIs
 
- View Pending Leave Requests
- Approve Leave Request
- Reject Leave Request
 
### 8. Import Postman Collection
 
Import the provided Postman collection and execute all scenarios.
 
---
 
## Access URLs
 
### Application
 
| Service | URL |
|----------|------|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:8080 |
 
### Swagger Documentation
 
| Service | URL |
|----------|------|
| Auth Service | http://localhost:8081/swagger-ui.html |
| Employee Service | http://localhost:8082/swagger-ui.html |
| Leave Service | http://localhost:8083/swagger-ui.html |
| Notification Service | http://localhost:8084/swagger-ui.html |
 
---
 
## Monitoring
 
| Component | URL |
|------------|------|
| Zipkin | http://localhost:9411 |
| Kibana | http://localhost:5601 |
| Elasticsearch | http://localhost:9200 |
 
---
 
## RabbitMQ Management Console
 
| Component | URL |
|------------|------|
| RabbitMQ Dashboard | http://localhost:15672 |
 
### Default Credentials
 
```text
Username: guest
Password: guest
```
 
---
 
## Postman Collection
 
The complete Postman collection is included in the repository.
 
```text
postman/Leave Management.postman_collection.json
```
 
The collection contains:
 
- Authentication APIs
- Employee APIs
- Manager APIs
- Leave Management APIs
- Sample requests and responses
 
---
 
## Docker Hub Images
 
The application images are published on Docker Hub and can be pulled directly using:
 
```text
sahilsoni234/apigateway
sahilsoni234/authservice
sahilsoni234/employeeservice
sahilsoni234/leaveservice
sahilsoni234/notificationservice
sahilsoni234/eurekaserver
```
 
---
 
## Demo Video
 
Demo Recording Link:
 
```text
PASTE_YOUR_VIDEO_LINK_HERE
```
 
---
 
## Technology Stack
 
- Java 17
- Spring Boot 3
- Spring Cloud
- Spring Security
- Spring Data JPA
- MySQL
- RabbitMQ
- Eureka
- API Gateway
- Resilience4j
- Zipkin
- Elasticsearch
- Kibana
- Filebeat
- Docker
- Docker Compose
 
---
 
## Author
 
Sahil Soni
 