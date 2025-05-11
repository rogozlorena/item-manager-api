# Item Processing API

This is a Spring Boot RESTful API.  
It manages a collection of items, allowing basic CRUD operations, validation (including email), and asynchronous processing of item data.

## ✅ Features

- **CRUD Operations** for `Item` entities
- **Email Validation** (via custom regex)
- **Input Validation** using Spring's validation framework
- **Asynchronous Processing** of items with status updates
- **Proper Error Handling** and informative status codes
- **Unit & Integration Tests** with high code coverage

## 📦 Tech Stack

- Spring Boot
- Spring Data JPA
- H2 / PostgreSQL (pluggable)
- Jakarta Validation
- JUnit & Mockito

## 🧪 Testing

Tests are included for both:
- **Service Layer** (`ItemServiceTest.java`)
- **Controller Layer** (`ItemControllerTest.java`)

To run the tests:
```bash
./mvnw test
