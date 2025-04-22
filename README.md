# School Management System

A Spring Boot application for managing students, teachers, and courses in a school setting. The project provides a RESTful API with full CRUD support, pagination, and filtering, along with comprehensive Swagger documentation.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [API Behavior](#api-behavior)
- [Project Structure](#project-structure)

## Features

- CRUD operations for Students, Teachers, and Courses
- Pagination and dynamic filtering via JSON probes
- Contextual API behavior based on resource nesting
- OpenAPI (Swagger) documentation

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- H2
- Lombok
- Swagger (OpenAPI 3)

## Getting Started

### Prerequisites

- Java 21+
- Maven

### Run the App

```bash
git clone https://github.com/pavelpetkovvv/school.git
cd school
./mvnw spring-boot:run
```

The application will start at `http://localhost:8080`.

### Access Swagger UI

Visit: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## API Documentation

All endpoints are annotated with OpenAPI-compatible Swagger annotations. You can test and explore them via the Swagger UI.

## API Behavior

### Top-Level Endpoints

Resources like `/students`, `/teachers`, and `/courses` follow a consistent REST pattern:

#### Create

```http
POST /students
Content-Type: application/json
{
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Read (with optional filtering)

```http
GET /students
Content-Type: application/json
{
  "lastName": "Doe"
}
```

- Request body can contain a probe object (partial entity).
- Returns a `PageResult` with matching elements and total count (of matching elements).

#### Update

```http
PUT /students
Content-Type: application/json
{
  "id": "UUID",
  "firstName": "Jane"
}
```

#### Delete

```http
DELETE /students/{id}
```

### Nested Endpoints

When accessing nested resources like `/courses/{id}/students`, the API behaves differently:

- It returns only the students related to that specific course.
- Filtering is done via query parameters (e.g., `group`, `minAge`) rather than request body probes.
- This allows for more complex conditions such as `age > n`.

#### Example

```http
GET /courses/1234-5678-9012/students?minAge=22
```

- Lists all students enrolled in the course with ID `1234-5678-9012` and age > 22.

## Project Structure

```txt
school/
├── src/main/java/com/school/management/
│   ├── constants/         # Constants and message templates
│   ├── controllers/       # REST controllers (Students, Teachers, Courses)
│   ├── dto/               # Data Transfer Objects
│   ├── models/            # Entity classes
│   ├── repositories/      # Spring Data JPA repositories
│   ├── services/          # Business logic
│   └── SchoolApplication.java
├── src/main/resources/
│   └── application.yml
└── README.md
```
