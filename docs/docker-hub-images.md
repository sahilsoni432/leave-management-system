# Docker Hub Images
 
## Overview
 
All microservices of the Leave Management System are containerized using Docker and published to Docker Hub.
 
Docker images can be pulled and deployed independently or through Docker Compose.
 
Docker Hub Username:
 
```text
sahilsoni234
```
 
---
 
# Published Images
 
| Service | Docker Image | Tag |
|-----------|-------------|------|
| API Gateway | sahilsoni234/apigateway | latest |
| Auth Service | sahilsoni234/authservice | latest |
| Employee Service | sahilsoni234/employeeservice | latest |
| Eureka Server | sahilsoni234/eurekaserver | latest |
| Leave Service | sahilsoni234/leaveservice | latest |
| Notification Service | sahilsoni234/notificationservice | latest |
 
---
 
# Pull Commands
 
## API Gateway
 
```bash
docker pull sahilsoni234/apigateway:latest
```
 
## Auth Service
 
```bash
docker pull sahilsoni234/authservice:latest
```
 
## Employee Service
 
```bash
docker pull sahilsoni234/employeeservice:latest
```
 
## Eureka Server
 
```bash
docker pull sahilsoni234/eurekaserver:latest
```
 
## Leave Service
 
```bash
docker pull sahilsoni234/leaveservice:latest
```
 
## Notification Service
 
```bash
docker pull sahilsoni234/notificationservice:latest
```
 
---
 
# Image Purpose
 
## API Gateway
 
Acts as the single entry point for all client requests and routes traffic to downstream services.
 
---
 
## Auth Service
 
Handles user authentication and JWT token generation.
 
---
 
## Employee Service
 
Manages employee information and leave balances.
 
---
 
## Leave Service
 
Handles leave application, approval, rejection and leave history management.
 
---
 
## Notification Service
 
Consumes leave-related events from RabbitMQ and processes notifications.
 
---
 
## Eureka Server
 
Provides service discovery and registration for all microservices.
 
---
 
# Deployment
 
Images can be deployed individually using Docker containers or collectively using Docker Compose.
 
Example:
 
```bash
docker-compose -f docker-compose-hub.yaml up -d
```
 
This command pulls all images from Docker Hub and starts the complete Leave Management System environment.
 
---
 
# Containerization Benefits
 
- Consistent deployment across environments
- Simplified application distribution
- Faster service startup
- Easy scalability
- Version-controlled deployments
- Cloud-ready deployment model
 
---
 
# Dockerized Services
 
The following services are fully containerized:
 
```text
API Gateway
Auth Service
Employee Service
Leave Service
Notification Service
Eureka Server
```
 
All services are available through Docker Hub repositories under:
 
```text
sahilsoni234/*
```
 