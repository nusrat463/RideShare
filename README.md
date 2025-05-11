# 🚗 RideShare Microservices Backend

RideShare is a Spring Boot-based backend application split into two microservices: `User Service` and `Rider Service`. It uses **RabbitMQ** for asynchronous communication 
between services and includes a simple CI/CD pipeline using **Docker**, **GitHub Actions**, and deployment to a free cloud platform.

---

## 🧩 Architecture

- **User Service**:  
  Allows users to request rides. Publishes ride requests to RabbitMQ.

- **Rider Service**:  
  Listens for ride requests via RabbitMQ, accepts the ride, and updates the status.

- **RabbitMQ**:  
  Acts as a message broker between services to decouple them.

---

## 🛠️ Tech Stack

- Java 17 + Spring Boot
- RabbitMQ
- Docker
- GitHub Actions (CI/CD)
- Render / Railway / Fly.io (Deployment)


