# 🚗 RideShare Microservices Backend

RideShare is a Spring Boot-based backend application follows a monorepo microservice architecture: `User Service` and `Rider Service`. It uses **RabbitMQ** for asynchronous communication between services, **WebSocket (STOMP)** for real-time communication with frontend clients and includes a simple CI/CD pipeline using **Docker**, **GitHub Actions**, and deployment to a free cloud platform.

---

## 🧩 Features

- **User Service**:  
  Allows users to request rides. Publishes ride requests to RabbitMQ.

- **Rider Service**:  
  - Riders periodically publish location updates via RabbitMQ + WebSocket.
  - Riders receive real-time ride assignment notifications via WebSocket.
  - Riders have 15 seconds to respond. On timeout or rejection, retry occurs.
  - If a rider rejects/does not respond, next nearest rider is tried (up to 3 attempts).

- **RabbitMQ**:  
  - Decoupled services communicate via event queues.
  - After 3 failed attempts to assign a ride, request is logged and sent to DLQ.

---

## 🛠️ Tech Stack

- Java 8 + Spring Boot
- RabbitMQ
- WebSocket
- Docker
- GitHub Actions (CI/CD)
- Render(Deployment)


