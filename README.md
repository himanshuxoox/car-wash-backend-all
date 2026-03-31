[README.md](https://github.com/user-attachments/files/26157485/README.md)
# 🚗 CarWash - Backend Microservices

A modern, scalable car wash booking platform built with Spring Boot microservices architecture.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Redis](https://img.shields.io/badge/Redis-7.0-red)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Services](#services)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Configuration](#configuration)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

CarWash is a comprehensive car wash booking platform that allows users to book various car wash services at their location. The backend is built using microservices architecture with Spring Boot, providing scalability, maintainability, and high availability.

### Key Highlights

- 📱 **SMS-based OTP Authentication** - Passwordless login using Fast2SMS
- 🗺️ **Location-based Services** - GPS-enabled service booking
- 📦 **Microservices Architecture** - Independent, scalable services
- 🔐 **JWT Security** - Centralized authentication via API Gateway
- 💾 **Redis Caching** - Fast OTP storage and validation
- 🚀 **RESTful APIs** - Clean, well-documented endpoints

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway (8080)                      │
│              ┌──────────────────────────────┐              │
│              │  JWT Authentication Filter   │              │
│              │  CORS Configuration          │              │
│              │  Request/Response Logging    │              │
│              └──────────────────────────────┘              │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────┐      ┌──────────────┐     ┌──────────────┐
│Auth Service  │      │User Service  │     │Booking       │
│  (8081)      │      │  (8082)      │     │Service       │
│              │      │              │     │  (8083)      │
│ • OTP Gen    │      │ • Profiles   │     │ • Bookings   │
│ • OTP Verify │      │ • Addresses  │     │ • Services   │
│ • JWT Token  │      │ • User Data  │     │ • Status     │
└──────────────┘      └──────────────┘     └──────────────┘
        │                     │                     │
        ▼                     ▼                     ▼
   ┌────────┐          ┌──────────────────────────────┐
   │ Redis  │          │      PostgreSQL DB           │
   │ Cache  │          │  • users                     │
   │        │          │  • bookings                  │
   └────────┘          └──────────────────────────────┘
```

---

## ✨ Features

### Authentication
- ✅ SMS OTP-based authentication (no passwords)
- ✅ JWT token generation and validation
- ✅ Rate limiting for OTP requests
- ✅ 5-minute OTP expiration
- ✅ 3 OTP attempts per request

### User Management
- ✅ User profile creation and updates
- ✅ Address management with GPS coordinates
- ✅ Profile completion tracking
- ✅ Phone number as unique identifier

### Booking System
- ✅ Multiple service types (Basic Wash, Premium Wash, Interior Clean, Full Service)
- ✅ Location-based service booking
- ✅ Booking status management (PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED)
- ✅ Booking history and tracking
- ✅ User-specific booking retrieval

### Security
- ✅ JWT-based authentication
- ✅ Centralized security at API Gateway
- ✅ CORS configuration
- ✅ Request validation
- ✅ User data isolation

---

## 🛠️ Tech Stack

### Backend Framework
- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Cloud Gateway** - API Gateway
- **Spring Data JPA** - Data persistence
- **Spring Security** - Authentication & authorization

### Databases
- **PostgreSQL 15** - Primary database
- **Redis 7.0** - Caching & session storage

### External Services
- **Fast2SMS** - SMS OTP delivery
- **JWT (JJWT)** - Token generation and validation

### Build Tools
- **Maven** - Dependency management and build

### Development Tools
- **Lombok** - Reduce boilerplate code
- **Validation** - Bean validation

---

## 🎯 Services

### 1️⃣ API Gateway (`api-gateway`)
**Port:** 8080

**Responsibilities:**
- Route requests to appropriate microservices
- JWT token validation
- CORS handling
- Request/response logging
- Load balancing (future: with service discovery)

**Routes:**
```yaml
/auth/**     → Auth Service (8081)
/users/**    → User Service (8082)
/bookings/** → Booking Service (8083)
```

---

### 2️⃣ Auth Service (`auth-service`)
**Port:** 8081

**Endpoints:**
```
POST /auth/send-otp        - Send OTP to phone number
POST /auth/verify-otp      - Verify OTP and generate JWT
POST /auth/resend-otp      - Resend OTP
```

**Features:**
- Generate 6-digit OTP
- Store OTP in Redis (5-minute TTL)
- Send SMS via Fast2SMS API
- Verify OTP against Redis
- Generate JWT token (7-day expiration)
- Rate limiting: 5 requests per 30 minutes

**Technologies:**
- Spring Boot
- Redis
- JWT (JJWT)
- Fast2SMS API
- RestTemplate

---

### 3️⃣ User Service (`user-service`)
**Port:** 8082

**Endpoints:**
```
GET  /users/profile        - Get user profile
POST /users/profile        - Create/Update user profile
PUT  /users/profile        - Update user profile
PATCH /users/profile/location - Update location only
```

**Features:**
- User profile management
- Address with GPS coordinates
- Profile completion tracking
- User data validation

**Database Schema:**
```sql
users table:
├── id (BIGINT, PRIMARY KEY)
├── phone_number (VARCHAR, UNIQUE)
├── name (VARCHAR)
├── email (VARCHAR)
├── address_line1, address_line2
├── city, state, postal_code
├── country
├── latitude, longitude (DECIMAL)
├── profile_completed (BOOLEAN)
├── created_at, updated_at (TIMESTAMP)
```

---

### 4️⃣ Booking Service (`booking-service`)
**Port:** 8083

**Endpoints:**
```
POST   /bookings                  - Create new booking
GET    /bookings                  - Get all user bookings
GET    /bookings/{id}             - Get booking by ID
GET    /bookings/status/{status}  - Get bookings by status
PATCH  /bookings/{id}/status      - Update booking status
DELETE /bookings/{id}             - Cancel booking
```

**Service Types:**
- **BASIC_WASH** - ₹299
- **PREMIUM_WASH** - ₹499
- **INTERIOR_CLEAN** - ₹399
- **FULL_SERVICE** - ₹799

**Booking Status:**
- PENDING
- CONFIRMED
- IN_PROGRESS
- COMPLETED
- CANCELLED

**Database Schema:**
```sql
bookings table:
├── id (BIGSERIAL, PRIMARY KEY)
├── phone_number (VARCHAR)
├── user_name (VARCHAR)
├── service_type (VARCHAR)
├── price (DECIMAL)
├── scheduled_date_time (TIMESTAMP)
├── status (VARCHAR)
├── address_line1, address_line2
├── city, state, postal_code
├── latitude, longitude (DECIMAL)
├── vehicle_type, vehicle_number
├── special_instructions (TEXT)
├── created_at, updated_at (TIMESTAMP)
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 15+
- Redis 7.0+
- Fast2SMS API key (for SMS)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/himanshuxoox/car-wash-backend-all.git
cd carwash-backend
```

2. **Set up PostgreSQL**
```bash
# Create database
psql -U postgres
CREATE DATABASE carwash_db;
\q
```

3. **Set up Redis**
```bash
# Start Redis server
redis-server

# Verify Redis is running
redis-cli ping
# Should return: PONG
```

4. **Configure environment variables**

Create `.env` file in each service directory:

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/carwash_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# Redis Configuration (auth-service)
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=your-secret-key-min-256-bits-long-for-security

# Fast2SMS Configuration (auth-service)
FAST2SMS_API_KEY=your_fast2sms_api_key
```

5. **Build all services**
```bash
# Build all services
./build-all.sh

# Or build individually
cd api-gateway && mvn clean install && cd ..
cd auth-service && mvn clean install && cd ..
cd user-service && mvn clean install && cd ..
cd booking-service && mvn clean install && cd ..
```

6. **Run all services**

Open 4 terminal windows:

```bash
# Terminal 1 - API Gateway
cd api-gateway
mvn spring-boot:run

# Terminal 2 - Auth Service
cd auth-service
mvn spring-boot:run

# Terminal 3 - User Service
cd user-service
mvn spring-boot:run

# Terminal 4 - Booking Service
cd booking-service
mvn spring-boot:run
```

7. **Verify services are running**
```bash
# Check API Gateway
curl http://localhost:8080/actuator/health

# Check Auth Service
curl http://localhost:8081/actuator/health

# Check User Service
curl http://localhost:8082/actuator/health

# Check Booking Service
curl http://localhost:8083/actuator/health
```

---

## 📚 API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication Flow

#### 1. Send OTP
```bash
curl -X POST http://localhost:8080/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "8299178503"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "OTP sent successfully",
  "remainingAttempts": 2
}
```

#### 2. Verify OTP
```bash
curl -X POST http://localhost:8080/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "8299178503",
    "otp": "123456"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Mjk5MTc4NTAzIi...",
  "phone": "8299178503"
}
```

### User Profile

#### 3. Get User Profile
```bash
curl -X GET http://localhost:8080/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "id": 1,
  "phoneNumber": "8299178503",
  "name": "Himanshu Singh",
  "email": "himanshu@example.com",
  "address": {
    "line1": "123 Main St",
    "line2": "Apartment 4B",
    "city": "New Delhi",
    "state": "Delhi",
    "postalCode": "110075",
    "country": "India",
    "latitude": 28.579726,
    "longitude": 77.055004
  },
  "profileCompleted": true,
  "createdAt": "2026-02-21T10:30:00",
  "updatedAt": "2026-02-21T10:30:00"
}
```

#### 4. Create/Update Profile
```bash
curl -X POST http://localhost:8080/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Himanshu Singh",
    "email": "himanshu@example.com",
    "address": {
      "line1": "123 Main St",
      "city": "New Delhi",
      "state": "Delhi",
      "postalCode": "110075",
      "latitude": 28.579726,
      "longitude": 77.055004
    }
  }'
```

### Bookings

#### 5. Create Booking
```bash
curl -X POST http://localhost:8080/bookings?userName=Himanshu \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceType": "BASIC_WASH",
    "price": 299,
    "scheduledDateTime": "2026-02-22T10:00:00",
    "addressLine1": "123 Main St",
    "city": "New Delhi",
    "state": "Delhi",
    "postalCode": "110075",
    "latitude": 28.579726,
    "longitude": 77.055004
  }'
```

**Response:**
```json
{
  "id": 1,
  "phoneNumber": "8299178503",
  "userName": "Himanshu Singh",
  "serviceType": "BASIC_WASH",
  "price": 299.00,
  "scheduledDateTime": "2026-02-22T10:00:00",
  "status": "PENDING",
  "addressLine1": "123 Main St",
  "city": "New Delhi",
  "state": "Delhi",
  "createdAt": "2026-02-21T12:00:00"
}
```

#### 6. Get All Bookings
```bash
curl -X GET http://localhost:8080/bookings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 7. Get Bookings by Status
```bash
curl -X GET http://localhost:8080/bookings/status/PENDING \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 8. Cancel Booking
```bash
curl -X DELETE http://localhost:8080/bookings/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🗄️ Database Schema

### ERD (Entity Relationship Diagram)

```
┌─────────────────┐         ┌─────────────────┐
│     users       │         │    bookings     │
├─────────────────┤         ├─────────────────┤
│ id (PK)         │         │ id (PK)         │
│ phone_number    │◄────────│ phone_number    │
│ name            │         │ user_name       │
│ email           │         │ service_type    │
│ address_line1   │         │ price           │
│ address_line2   │         │ scheduled_dt    │
│ city            │         │ status          │
│ state           │         │ address_*       │
│ postal_code     │         │ latitude        │
│ country         │         │ longitude       │
│ latitude        │         │ vehicle_*       │
│ longitude       │         │ special_inst    │
│ profile_done    │         │ created_at      │
│ created_at      │         │ updated_at      │
│ updated_at      │         └─────────────────┘
└─────────────────┘
```

### Indexes
```sql
-- User Service
CREATE INDEX idx_users_phone ON users(phone_number);

-- Booking Service
CREATE INDEX idx_bookings_phone ON bookings(phone_number);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_created ON bookings(created_at DESC);
```

---

## 🔐 Security

### JWT Token Structure
```json
{
  "sub": "8299178503",          // Phone number
  "userId": "uuid-here",         // User UUID
  "iat": 1771422908,            // Issued at
  "exp": 1771509308             // Expires at (7 days)
}
```

### Security Flow
```
1. User sends OTP request
   ↓
2. API Gateway allows (no auth required)
   ↓
3. Auth Service generates OTP and sends SMS
   ↓
4. User verifies OTP
   ↓
5. Auth Service generates JWT token
   ↓
6. User includes token in subsequent requests
   ↓
7. API Gateway validates JWT
   ↓
8. Gateway extracts phone number from JWT
   ↓
9. Gateway adds X-User-Phone header
   ↓
10. Services use phone number for authorization
```

### CORS Configuration
```yaml
allowed-origins: "*"
allowed-methods: "GET, POST, PUT, PATCH, DELETE, OPTIONS"
allowed-headers: "*"
allow-credentials: true
```

---

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/carwash_db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - |
| `SPRING_REDIS_HOST` | Redis host | `localhost` |
| `SPRING_REDIS_PORT` | Redis port | `6379` |
| `JWT_SECRET` | JWT signing key | - |
| `FAST2SMS_API_KEY` | Fast2SMS API key | - |

### Application Ports

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8080 | Main entry point |
| Auth Service | 8081 | Authentication |
| User Service | 8082 | User management |
| Booking Service | 8083 | Booking management |

---

## 🐳 Deployment

### Docker Compose

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: carwash_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    environment:
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - auth-service
      - user-service
      - booking-service

  auth-service:
    build: ./auth-service
    ports:
      - "8081:8081"
    environment:
      - SPRING_REDIS_HOST=redis
      - JWT_SECRET=${JWT_SECRET}
      - FAST2SMS_API_KEY=${FAST2SMS_API_KEY}
    depends_on:
      - redis

  user-service:
    build: ./user-service
    ports:
      - "8082:8082"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/carwash_db
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      - postgres

  booking-service:
    build: ./booking-service
    ports:
      - "8083:8083"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/carwash_db
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### Run with Docker
```bash
docker-compose up -d
```

---

## 📊 Monitoring & Logging

### Actuator Endpoints
```
/actuator/health     - Health check
/actuator/info       - Application info
/actuator/metrics    - Application metrics
```

### Logging Configuration
```yaml
logging:
  level:
    root: INFO
    com.carwash: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

### Log Format
```
2026-02-21 12:00:00.000 INFO [service-name] [thread] logger - message
```

---

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```



---

## 🚧 Roadmap

- [ ] Add service discovery (Eureka)
- [ ] Implement circuit breaker (Resilience4j)
- [ ] Add distributed tracing (Sleuth + Zipkin)
- [ ] Implement rate limiting
- [ ] Add admin dashboard service
- [ ] Real-time notifications (WebSocket)
- [ ] Payment integration (Razorpay)
- [ ] Service provider app APIs
- [ ] Advanced analytics service
- [ ] Multi-language support

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards
- Follow Java naming conventions
- Write unit tests for new features
- Update documentation
- Use meaningful commit messages

---



## 👥 Authors

- **Himanshu Singh** - *Initial work* - [@himanshusingh](https://github.com/himanshuxoox)

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Fast2SMS for SMS OTP service
- PostgreSQL and Redis communities
- All contributors

---

## 📞 Support

For support, email himanshusingh1768@gmail.com or open an issue on GitHub.

---

## 🔗 Links

- [Frontend Repository](https://github.com/himanshuxoox/CarWashApp_mobileapp)

---

<div align="center">
  <p>Made with ❤️ by Himanshu Singh</p>
  <p>⭐ Star this repository if you find it helpful!</p>
</div>
