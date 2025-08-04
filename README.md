# 🏦 DigitalBank API

A secure digital banking REST API built using Spring Boot. It supports user and account management, transactions, interest tracking, and more. Includes JWT-based authentication, pagination, and rate limiting via Bucket4j.

---

## 📌 Features

- ✅ User Registration & Login (with JWT)
- ✅ Account Creation
- ✅ Deposit / Withdrawal / Transfer
- ✅ Balance Check
- ✅ Mini Statement (Last 5 Transactions)
- ✅ Account Summary
- ✅ CSV Export of Transactions
- ✅ Search Transactions by Type and Date
- ✅ Admin Dashboard
- ✅ Interest Calculation History
- ✅ **Pagination Support** (for transactions)
- ✅ **Rate Limiting** (100 requests per hour per user)

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 17+
- Maven or Gradle
- PostgreSQL or any SQL database
- Postman or Swagger UI
- (Optional) Docker

---

## 🔐 Authentication

### 🔒 Login

```
POST /api/users/login
```

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "password"
}
```

**Response**
```json
{
  "status": "success",
  "data": "<JWT_TOKEN>"
}
```

> 🔑 Use the JWT in `Authorization: Bearer <token>` for all protected endpoints.

---

## 👤 User & Account

### ✅ Register User

```
POST /api/users/register
```

### ✅ Create Account

```
POST /api/accounts/create
```

---

## 💵 Transactions

### Deposit

```
POST /api/accounts/deposit
```

### Withdraw

```
POST /api/accounts/withdraw
```

### Transfer

```
POST /api/accounts/transfer
```

---

## 📜 Mini Statement

```
GET /api/accounts/{accountNumber}/mini-statement
```

---

## 📂 Export Transactions (CSV)

```
GET /api/accounts/{accountNumber}/transactions/export
```

Optional query parameters:

- `fromDate` – e.g., `2025-01-01`
- `toDate` – e.g., `2025-08-01`

---

## 📊 Paginated Transaction History

```
GET /api/users/transaction/{accountNumber}?page=0&size=10
```

---

## 🔎 Search Transactions

```
GET /api/accounts/{accountNumber}/transactions/search
```

**Query Parameters**
- `type`: `DEPOSIT`, `WITHDRAW`, `TRANSFER`
- `from`: Date (ISO)
- `to`: Date (ISO)

---

## 📈 Admin Dashboard

```
GET /api/admin/dashboard
```

---

## 💸 Interest Transaction History

```
GET /api/admin/interest-history
```

---

## 🛡️ Rate Limiting (via Bucket4j)

- Each authenticated user is allowed **100 transaction-related requests per hour**.
- On exceeding the limit, response code: `429 Too Many Requests`.

```json
{
  "message": "🚫 Rate limit exceeded. Try again later."
}
```

---

## 🔧 Technologies Used

- Spring Boot
- Spring Security + JWT
- Hibernate + JPA
- Bucket4j (rate limiting)
- PostgreSQL / MySQL
- Swagger (OpenAPI 3)
- JUnit + Mockito

---

## 🧪 Testing

Tests are located under:

```
/src/test/java/com/bank/DigitalBank/
```

Run tests using:
```bash
./gradlew test
# or
mvn test
```

---

## 📦 Future Improvements

- ✅ Docker & Docker Compose
- ⬜ Kafka for Transaction Logs
- ⬜ Role-based Access Control (RBAC)
- ⬜ Frontend dashboard (React/Vue)

---

## 🧑‍💻 Author

**Soumya Pokale**

📧 `soumyapokale@gmail.com`  
🔗 [LinkedIn](https://www.linkedin.com/in/soumyapokale)  
💼 [GitHub](https://github.com/soumyapokale)