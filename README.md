# DigitalBank

# 💳 Digital Banking System

A secure and scalable **Digital Banking API** built with Spring Boot. It enables basic banking operations such as account creation, deposits, withdrawals, transfers, and balance checking — all through RESTful APIs.

---

## 🔧 Technologies Used

- Java 17+
- Spring Boot
- Spring Data JPA
- Spring Validation
- Hibernate
- MySQL / H2 (for dev/test)
- Lombok
- Swagger / Springdoc OpenAPI
- SLF4J (logging)
- Gradle / Maven

---

## 🚀 How to Run Locally

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/digital-banking.git
   cd digital-banking
Configure the database
Open src/main/resources/application.properties and set:

properties

spring.datasource.url=jdbc:mysql://localhost:3306/digitalbank
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
Run the application

If using Gradle:

bash
./gradlew bootRun
Or if using Maven:

bash
./mvnw spring-boot:run
Access Swagger API docs

bash
http://localhost:8080/swagger-ui.html
✅ Features Implemented
👤 User Registration

🏦 Bank Account Creation

💰 Deposit Cash

💸 Withdraw Funds

🔁 Transfer Between Accounts

📊 Check Account Balance

✅ Request Validation with @Valid

❗ Centralized Exception Handling

📄 Interactive API Docs (Swagger)

🧪 How to Run Unit Tests
▶️ Using IntelliJ IDEA
Right-click on the test directory or any test class.

Select "Run 'All Tests'" or "Run <TestClass>".

You will see results in the Run/Test window.

👨‍💻 Author
Soumya
📧 soumyapokale41@gmail.com


✨ Contributions, issues, and suggestions are welcome! ✨


Let me know if you'd like me to:
- Add badges (build status, license, etc.)
- Include example request/response JSON
- Document API endpoints in the README
- Add a Docker section if you're planning containerization.
