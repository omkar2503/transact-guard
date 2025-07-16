# Transact Guard

Transact Guard is a Spring Boot application for detecting financial fraud using rule-based logic. It features user registration, login, transaction management, and real-time fraud detection, with a simple web interface powered by Thymeleaf and MongoDB as the backend database.

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Web (RESTful APIs)
- Spring Data MongoDB
- Thymeleaf (server-side HTML rendering)
- Maven
- MongoDB
- YAML-based configuration

## Features
- User registration and login (username/password)
- Dashboard: view balance, transactions, and fraud alerts
- Send money to other users
- View fraud alerts triggered on your account
- Rule-based fraud detection (high amount, velocity, unusual pattern, frequency spike, duplicate transaction, failed attempts + high-value)

## Data Models (MongoDB Collections)
- **User**: userId, username, password, accountBalance
- **Transaction**: id, userId, recipientId, amount, timestamp
- **FraudTransaction**: id, transactionId, userId, recipientId, description, timestamp
- **LoginAttempt**: id, userId, timestamp, success, type ("login"/"transaction")

## Project Structure
```
- controller/
- service/
- repository/
- model/
- config/
- templates/ (Thymeleaf HTML files)
```

## Getting Started

### Prerequisites
- Java 17+
- Maven
- MongoDB (running locally on default port 27017)

### Setup
1. Clone the repository:
   ```sh
   git clone <your-repo-url>
   cd transact-guard
   ```
2. Configure MongoDB connection in `src/main/resources/application.yml` if needed.
3. Build and run the application:
   ```sh
   mvn spring-boot:run
   ```
"# transact-guard" 
