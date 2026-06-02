# Leave Management System
 
A Spring Boot Microservices based Leave Management System implementing service discovery, API gateway, distributed tracing, centralized logging, and containerized deployment using Docker.
 
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
- RabbitMQ Event Driven Communication
- Distributed Tracing using Zipkin
- Centralized Logging using ELK Stack
- Dockerized Deployment
- Docker Hub Image Deployment
 
---
 
## Running Locally
 
### Build Images
 
```bash
docker compose build --no-cache
```
 
### Start Application
 
```bash
docker compose up -d
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
 
## Access URLs
 
### Application
 
| Service | URL |
|----------|----------|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:8080 |
 
### Swagger Documentation
 
| Service | URL |
|----------|----------|
| Auth Service | http://localhost:8081/swagger-ui.html |
| Employee Service | http://localhost:8082/swagger-ui.html |
| Leave Service | http://localhost:8083/swagger-ui.html |
| Notification Service | http://localhost:8084/swagger-ui.html |
 
### Monitoring
 
| Component | URL |
|------------|------------|
| Zipkin | http://localhost:9411 |
| Kibana | http://localhost:5601 |
| Elasticsearch | http://localhost:9200 |
 
### RabbitMQ Management Console
 
| Component | URL |
|------------|------------|
| RabbitMQ Dashboard | http://localhost:15672 |
 
Default Credentials:
 
```text
Username: guest
Password: guest
```
 
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
- Zipkin
- Elasticsearch
- Kibana
- Filebeat
- Docker
- Docker Compose
 
---
 
## Author
 
Sahil Soni
 