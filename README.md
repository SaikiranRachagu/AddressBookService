# 📒 Address Book REST API

A Spring Boot RESTful API that allows users to manage multiple address books and their contacts in Reece group.

---

## ✨ Features

- Create and manage multiple address books
- Add, update, and delete contacts from an address book
- List all contacts in a specific address book
- Retrieve a unique set of contacts across all address books
- In-memory data storage
- Swagger (OpenAPI 3.0) documentation

---

## 🛠 Tech Stack

- Java 17+
- Spring Boot
- Spring Web
- Swagger/OpenAPI (via `springdoc-openapi-ui`)

---

## 🚀 Running the Application

### Prerequisites

- Java 17 or higher
- Maven

### Steps
Step 1: Build your Spring Boot JAR
mvn clean package

Step 2: Build the Docker image <Note:Dot is mandatory at the end of docker command>
docker build -t addressbookservice-app .

docker run -p 8080:8080 addressbookservice-app

```bash
git clone 
cd address-book-api
mvn spring-boot:run
