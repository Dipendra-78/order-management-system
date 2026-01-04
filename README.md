# 🛒 Order Management System (Spring Boot)

A **production-style backend REST API** for managing orders with **JWT authentication**, **role-based access control**, **caching**, **rate limiting**, and **admin reporting**.


This project simulates a real e-commerce order workflow and focuses on
security, scalability, and clean backend design rather than simple CRUD.


---

## 🚀 Tech Stack

- Java 17  
- Spring Boot 3.2  
- Spring Security (JWT)  
- Spring Data JPA  
- MySQL  
- Caffeine Cache  
- Bucket4j (Rate Limiting)  
- Swagger OpenAPI  
- JUnit 5 & MockMvc  
- Hibernate  

---

## 🔐 Security Features

- JWT-based stateless authentication  
- Role-Based Access Control (**USER / ADMIN**)  
- Method-level authorization using `@PreAuthorize`  
- Custom authentication & access-denied handlers  
- Secure password handling  

---

### Authentication Flow
1. User logs in → receives JWT
2. JWT is sent via Authorization header
3. Spring Security validates token
4. Access granted based on role (USER / ADMIN)


## ✨ Core Features

### 👤 User
- Register and login with JWT  
- Place orders  
- View own orders with pagination & sorting  
- Cancel eligible orders  
- View order status history  

### 🧑‍💼 Admin
- View all orders  
- Update order status (**PAID → SHIPPED → DELIVERED**)  
- Cancel orders with business rules  
- View system metrics and reports  

---

## 📊 Reports & Metrics

- Orders per day  
- Orders per day (date range)  
- Revenue summary  
- Order count by status  
- CSV export for reports  

---

## ⚡ Performance & Reliability

- In-memory caching using **Caffeine**  
- Rate limiting using **Bucket4j**  
- Database indexing for faster queries  
- Pagination & sorting for large datasets  

---

## 🛠 Developer Friendly

- Global exception handling  
- Validation with clear error responses  
- Swagger UI for API testing  
- Clean layered architecture  
  **Controller → Service → Repository**

---

## 📂 Project Structure

- controller — REST APIs  
- service / impl — Business logic  
- repository — Data access  
- entity — JPA entities  
- dto — API request/response models  
- security — JWT & security filters  
- config — Cache & Swagger config  
- exception — Global error handling  
- specification — Dynamic filtering



---

## 📌 API Overview

### 🔐 Authentication
- POST /api/v1/auth/login
- POST /api/v1/users/register

### 👤 User APIs
- POST /api/v1/orders
- GET /api/v1/orders
- PUT /api/v1/orders/{orderId}/cancel
- GET /api/v1/orders/{orderId}/history

### 🧑‍💼 Admin APIs
- PUT /api/v1/admin/orders/{id}/pay
- PUT /api/v1/admin/orders/{id}/ship
- PUT /api/v1/admin/orders/{id}/deliver
- GET /api/v1/admin/orders/all

### 📊 Reports & Metrics
- GET /api/v1/admin/reports/orders-per-day
- GET /api/v1/admin/reports/orders-per-day/range
- GET /api/v1/admin/metrics/summary
- GET /api/v1/admin/metrics/status
- GET /api/v1/admin/metrics/revenue

---

## 📖 API Documentation

Swagger UI is available at:

http://localhost:8080/swagger-ui

Supports **JWT authentication directly from the UI**.

---

## ▶️ How to Run

1. Clone the repository
   ```bash
   git clone <your-repo-url>
2. Configure MySQL in application.properties
3. Run the Spring Boot application
4. Open Swagger UI and test APIs
---
## 🧪 Testing

- Controller-level tests using MockMvc
- Authentication and validation scenarios covered
- Separate test classes per controller


## 📈 Learning Outcomes

- Implemented real-world Spring Security with JWT
- Designed RBAC-secured REST APIs
- Used caching & rate limiting for performance
- Applied clean architecture & best practices
- Gained confidence in backend system design

## Future Improvements

- Redis-based distributed caching
- Refresh token support
- Docker & CI/CD pipeline
- Event-driven notifications
- Advanced monitoring
