# 🛒 E-Commerce Microservices Project

[![Java](https://img.shields.io/badge/Java-21-ED8B00.svg?style=for-the-badge\&logo=openjdk\&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F.svg?style=for-the-badge\&logo=springboot\&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2024-6DB33F.svg?style=for-the-badge\&logo=spring\&logoColor=white)](https://spring.io/projects/spring-cloud)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-4169E1.svg?style=for-the-badge\&logo=postgresql\&logoColor=white)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600.svg?style=for-the-badge\&logo=rabbitmq\&logoColor=white)](https://www.rabbitmq.com/)
[![Keycloak](https://img.shields.io/badge/Keycloak-4D4D4D.svg?style=for-the-badge\&logo=keycloak\&logoColor=white)](https://www.keycloak.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED.svg?style=for-the-badge\&logo=docker\&logoColor=white)](https://www.docker.com/)

A **microservices-based e-commerce platform** developed with **Java, Spring Boot, and Spring Cloud**, designed to simulate the architecture and infrastructure of a modern scalable e-commerce system.

The project consists of multiple independent business services supported by centralized configuration, service discovery, API Gateway routing, asynchronous messaging, and distributed authentication and authorization.

---

## 📚 Table of Contents

* [Overview](#-overview)
* [Architecture](#-architecture)
* [Infrastructure Services](#-infrastructure-services)
* [Business Services](#-business-services)
* [Inter-Service Communication](#-inter-service-communication)
* [Security](#-security)
* [Technology Stack](#-technology-stack)
* [Project Structure](#-project-structure)
* [Prerequisites](#-prerequisites)
* [Installation](#-installation)
* [Running the Application](#-running-the-application)
* [Service Startup Order](#-service-startup-order)
* [API Gateway](#-api-gateway)
* [Notes](#-notes)
* [License](#-license)

---

## 📌 Overview

The project was developed as a **multi-module microservices application** to demonstrate the design and implementation of a distributed e-commerce platform.

The system is divided into two main categories:

* ⚙️ **Infrastructure Services**
* 📦 **Business Services**

Each business service is designed as an independent application and communicates with other services through **REST APIs** and **asynchronous messaging** where appropriate.

### 🎯 Main Goals

* Implement a scalable microservices architecture
* Separate business responsibilities into independent services
* Provide centralized configuration management
* Enable dynamic service discovery
* Implement API Gateway-based routing
* Support asynchronous inter-service communication
* Provide centralized authentication and authorization
* Containerize services using Docker

---

# 🏗️ Architecture

```text
                         ┌──────────────────┐
                         │      Client      │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │   API Gateway    │
                         └────────┬─────────┘
                                  │
             ┌────────────────────┼────────────────────┐
             │                    │                    │
             ▼                    ▼                    ▼
      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
      │    User     │      │   Product   │      │    Order    │
      │   Service   │      │   Service   │      │   Service   │
      └─────────────┘      └─────────────┘      └─────────────┘
             │                    │                    │
             └────────────────────┼────────────────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │    RabbitMQ      │
                         │ Message Broker   │
                         └──────────────────┘

      ┌──────────────────────────────────────────────────────┐
      │              Infrastructure Layer                    │
      │                                                      │
      │   Config Server     Discovery Server     Keycloak    │
      │      (Config)           (Eureka)        (Security)   │
      └──────────────────────────────────────────────────────┘
```

The architecture separates infrastructure responsibilities from business logic, allowing individual services to be developed and deployed independently.

---

# ⚙️ Infrastructure Services

## 🔧 Config Server

Centralizes configuration management for all microservices.

**Responsibilities:**

* Centralized application configuration
* Providing configuration files to services
* Reducing duplicated configuration
* Managing environment-specific settings

---

## 🔍 Discovery Server

Implemented using **Netflix Eureka**.

**Responsibilities:**

* Service registration
* Dynamic service discovery
* Service-to-service communication support
* Maintaining the registry of available services

---

## 🌐 API Gateway

The API Gateway acts as the **single entry point** for external requests.

**Responsibilities:**

* Request routing
* Service forwarding
* Centralized entry point
* Gateway-level security integration
* Request management

---

## 🔐 Keycloak

Keycloak is used to provide centralized authentication and authorization.

**Responsibilities:**

* User authentication
* JWT-based authentication
* Role-based authorization
* Centralized identity management

---

# 📦 Business Services

## 👤 User Service

Responsible for user-related operations.

**Features:**

* User registration
* User login
* Profile management
* User information management

---

## 📦 Product Service

Responsible for product management.

**Features:**

* Product creation and management
* Product information
* Category management
* Product properties

---

## 📊 Stock Service

Responsible for inventory operations.

**Features:**

* Stock tracking
* Stock updates
* Inventory management

---

## 🔎 Search Service

Provides product search and filtering functionality.

**Features:**

* Product search
* Filtering
* Query operations

---

## 🛒 Shopping Cart Service

Manages users' shopping carts.

**Features:**

* Add products to cart
* Remove products from cart
* Update cart contents
* Manage cart state

---

## ❤️ Favorite List Service

Manages users' favorite products.

**Features:**

* Add products to favorites
* Remove products from favorites
* Retrieve favorite products

---

## 📑 Order Service

Responsible for order management.

**Features:**

* Create orders
* Retrieve order history
* Track order status

---

## 💳 Payment Service

Responsible for payment-related operations.

**Features:**

* Payment processing
* Payment status management
* Payment provider integration

> Example integration: **Iyzico**

---

# 📨 Inter-Service Communication

The system supports communication between microservices through both synchronous and asynchronous approaches.

### 🔄 REST Communication

REST APIs are used for synchronous communication between services where an immediate response is required.

### 📨 RabbitMQ

**RabbitMQ** is used as the message broker for asynchronous communication.

This approach helps decouple services and supports event-driven communication between distributed components.

```text
Service A
    │
    │ Publish Event
    ▼
┌──────────────┐
│   RabbitMQ   │
└──────┬───────┘
       │
       │ Consume Event
       ▼
Service B
```

---

# 🔐 Security

The project uses **Keycloak** together with **OAuth2 and JWT** to secure distributed services.

### Authentication

```text
Client
   │
   │ Login
   ▼
Keycloak
   │
   │ JWT
   ▼
Client
```

The client then sends the JWT with subsequent requests.

### Authorization

Role-based authorization is used to control access to protected resources.

**Technologies:**

* Keycloak
* OAuth2
* JWT
* Role-Based Authorization
* Spring Security

---

# 🛠️ Technology Stack

### ☕ Backend

* Java 21
* Spring Boot
* Spring Data JPA
* Spring Cloud
* Spring Security

### 🏗️ Architecture

* Microservices
* RESTful APIs
* Event-Driven Communication
* Service Discovery
* API Gateway
* Centralized Configuration

### 🔧 Infrastructure

* Spring Cloud Config Server
* Netflix Eureka
* Spring Cloud Gateway
* RabbitMQ
* Keycloak

### 🗄️ Database

* PostgreSQL

### 🐳 DevOps

* Docker
* Maven

---

# 📁 Project Structure

The project is organized into a **frontend application** and a collection of independent **microservices**, along with the required infrastructure components.

```text
N11-TalentHub-Backend-Bootcamp---E-Commerce-Project/
│
├── frontend/
│
└── microservices/
    │
    ├── api-gateway/
    ├── config-server/
    ├── discovery-server/
    │
    ├── user-service/
    ├── product-service/
    ├── stock-service/
    ├── search-service/
    ├── shopping-cart-service/
    ├── favorite-list-service/
    ├── order-service/
    └── payment-service/
    │
    ├── build.bat
    └── docker-compose.yml
```

### ⚙️ Infrastructure

| Service            | Description                                             |
| :----------------- | :------------------------------------------------------ |
| `api-gateway`      | Central entry point for routing external requests       |
| `config-server`    | Centralized configuration management                    |
| `discovery-server` | Service registration and discovery using Netflix Eureka |

### 📦 Business Services

| Service                 | Description                                              |
| :---------------------- | :------------------------------------------------------- |
| `user-service`          | User registration, authentication and profile management |
| `product-service`       | Product and category management                          |
| `stock-service`         | Product inventory and stock management                   |
| `search-service`        | Product search and filtering                             |
| `shopping-cart-service` | Shopping cart operations                                 |
| `favorite-list-service` | User favorite product management                         |
| `order-service`         | Order creation and order management                      |
| `payment-service`       | Payment processing and payment-related operations        |

### 🐳 Deployment & Build

| File                 | Purpose                                                       |
| :------------------- | :------------------------------------------------------------ |
| `docker-compose.yml` | Defines and orchestrates the project's containerized services |
| `build.bat`          | Build script for the microservices                            |

The `microservices` directory contains both the **infrastructure services** and **business services**, while the `frontend` directory contains the client-side application.


---

# 🚀 Prerequisites

Before running the project, make sure the following tools are installed:

* **Java 21+**
* **Maven**
* **PostgreSQL**
* **RabbitMQ**
* **Docker**
* **Keycloak**

---

# 🔧 Installation

### 1. Clone the repository

```bash
git clone <YOUR_REPOSITORY_URL>
cd <PROJECT_DIRECTORY>
```

### 2. Configure PostgreSQL

Create the required PostgreSQL databases and update the corresponding service configurations.

Example:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/<database>
    username: <username>
    password: <password>
```

### 3. Start RabbitMQ

Make sure RabbitMQ is running before starting services that depend on asynchronous messaging.

### 4. Start Keycloak

Configure the required realm, clients, users, and roles according to the application's security configuration.

---

# ▶️ Running the Application

The services should be started in the following order to ensure that the infrastructure is available before business services start communicating.

---

# 🔢 Service Startup Order

### 1️⃣ Config Server

Start the centralized configuration server first.

```text
Config Server
      ↓
Provides configuration
      ↓
All other services
```

### 2️⃣ Discovery Server

Start Eureka after the Config Server.

```text
Discovery Server
      ↓
Service Registration
      ↓
Business Services
```

### 3️⃣ Business Services

Start the following services:

```text
user-service
product-service
stock-service
search-service
shopping-cart-service
favorite-list-service
order-service
payment-service
```

### 4️⃣ API Gateway

Start the API Gateway after the services have registered with Eureka.

```text
Client
  ↓
API Gateway
  ↓
Eureka
  ↓
Target Microservice
```

---

# 🌐 API Gateway

All external requests are intended to pass through the API Gateway.

Instead of accessing individual services directly:

```text
Client → User Service
Client → Product Service
Client → Order Service
```

the preferred architecture is:

```text
             ┌───────────────┐
             │    Client     │
             └───────┬───────┘
                     │
                     ▼
             ┌───────────────┐
             │ API Gateway   │
             └───────┬───────┘
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
     User         Product       Order
    Service       Service       Service
```

This provides a centralized entry point for routing and security-related concerns.

---

# 📌 Notes

* Each microservice is designed to operate independently.
* Services can communicate through REST APIs or asynchronous messaging.
* Eureka is responsible for service discovery.
* Config Server provides centralized configuration.
* RabbitMQ is used for asynchronous communication.
* Keycloak handles authentication and authorization.
* Docker can be used to containerize the services.
* The architecture is designed with scalability and independent deployment in mind.

---

# 📄 License

This project was developed for **educational and development purposes**.
