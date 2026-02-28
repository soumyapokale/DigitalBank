# 🏦 DigitalBank API

A secure digital banking REST API built using Spring Boot.
Supports account management, transactions, JWT authentication,
pagination, rate limiting, and Kafka-based event publishing.

------------------------------------------------------------

## 🚀 Features

- User Registration & Login (JWT)
- Account Creation
- Deposit / Withdraw / Transfer
- Balance Check
- Mini Statement (Last 5 Transactions)
- Paginated Transaction History
- Search Transactions (Type & Date)
- CSV Export
- Admin Dashboard
- Interest Transaction History
- Rate Limiting (100 req/hr using Bucket4j)
- Kafka Integration (Transfer Events)

------------------------------------------------------------

## 🛠 Tech Stack

- Spring Boot
- Spring Security + JWT
- Hibernate + JPA
- PostgreSQL / MySQL
- Bucket4j
- Apache Kafka
- Swagger (OpenAPI 3)
- JUnit + Mockito

------------------------------------------------------------

## 🔐 Authentication

POST /api/users/login

Request Body:
{
"email": "user@example.com",
"password": "password"
}

Response:
{
"status": "success",
"data": "<JWT_TOKEN>"
}

Use header:
Authorization: Bearer <JWT_TOKEN>

------------------------------------------------------------

## 👤 User & Account APIs

Register User:
POST /api/users/register

Create Account:
POST /api/accounts/create

------------------------------------------------------------

## 💵 Transaction APIs

Deposit:
POST /api/accounts/deposit

Withdraw:
POST /api/accounts/withdraw

Transfer:
POST /api/accounts/transfer

------------------------------------------------------------

## 📜 Mini Statement

GET /api/accounts/{accountNumber}/mini-statement

------------------------------------------------------------

## 📊 Paginated Transaction History

GET /api/users/transaction/{accountNumber}?page=0&size=10

------------------------------------------------------------

## 🔎 Search Transactions

GET /api/accounts/{accountNumber}/transactions/search?type=TRANSFER&from=2025-01-01&to=2025-08-01

------------------------------------------------------------

## 📂 Export Transactions (CSV)

GET /api/accounts/{accountNumber}/transactions/export

Optional:
- fromDate
- toDate

------------------------------------------------------------

## 📈 Admin APIs

Dashboard:
GET /api/admin/dashboard

Interest History:
GET /api/admin/interest-history

------------------------------------------------------------

## 🛡 Rate Limiting

100 transaction-related requests per hour per user.

If exceeded:
{
"message": "Rate limit exceeded. Try again later."
}

------------------------------------------------------------

## 📨 Kafka Integration

Every TRANSFER transaction is published to Kafka topic:

transaction-events

### application.properties

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

### Gradle Dependency

implementation 'org.springframework.kafka:spring-kafka'

### Producer Example

kafkaTemplate.send("transaction-events", transactionEvent);

------------------------------------------------------------

## ⚙️ Kafka Local Setup (Windows – Without Docker)

1) Start Zookeeper
   cd C:\kafka
   bin\windows\zookeeper-server-start.bat config\zookeeper.properties

2) Start Kafka
   bin\windows\kafka-server-start.bat config\server.properties

3) Create Topic
   bin\windows\kafka-topics.bat --create --topic transaction-events --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

4) Read Messages
   bin\windows\kafka-console-consumer.bat --topic transaction-events --from-beginning --bootstrap-server localhost:9092

------------------------------------------------------------

## 🧪 Run Tests

./gradlew test
or
mvn test

------------------------------------------------------------

## 📦 Future Improvements

- Docker & Docker Compose
- Role-Based Access Control (RBAC)
- Fraud Detection Microservice (Kafka Consumer)
- Frontend Dashboard (React)

------------------------------------------------------------

Author: Soumya Pokale
Email: soumyapokale@gmail.com
GitHub: https://github.com/soumyapokale
LinkedIn: https://www.linkedin.com/in/soumyapokale