# Project Initial Structure (Actual Current State)

> **Status:** This file documents the **actual current state** of the `medical-record` project.
> The project is in its **initial/empty state** — only the Spring Boot skeleton is present.
> No implementation code (controllers, entities, services, etc.) has been created yet.

## 1. General Information
- **Project Name:** medical-record
- **Group ID:** com.ivanovp
- **Version:** 0.0.1-SNAPSHOT
- **Build Tool:** Gradle
- **Main Application Class:** `com.ivanovp.medical_record.MedicalRecordApplication`

## 2. Technology Stack
- **Backend:** Java 25, Spring Boot 4.0.6
- **Dependencies Configured (in build.gradle):**
  - Spring Data JPA
  - Spring Security
  - Spring WebMVC
  - SpringDoc OpenAPI (Swagger) — v3.0.2
  - Lombok (compileOnly)
  - MSSQL JDBC Driver (runtimeOnly)
- **Test Dependencies:**
  - spring-boot-starter-data-jpa-test
  - spring-boot-starter-security-test
  - spring-boot-starter-webmvc-test
  - JUnit Platform
- **Frontend:** None yet (static/ and templates/ folders are empty)

## 3. Current Project Structure

### 3.1. Source Code (`src/main/java/com/ivanovp/medical_record/`)
```
src/main/java/com/ivanovp/medical_record/
└── MedicalRecordApplication.java    # Main entry point (default Spring Boot class)
```

**Note:** No other packages or files exist yet. The following packages need to be created:
- `config/` — Configuration classes
- `controller/` — REST Controllers
- `dto/` — Data Transfer Objects (request/response)
- `entity/` — JPA Entities
- `exception/` — Exception handling
- `repository/` — Spring Data JPA Repositories
- `security/` — Security (JWT, UserDetailsService)
- `service/` — Service Interfaces
- `service/impl/` — Service Implementations

### 3.2. Resources (`src/main/resources/`)
```
src/main/resources/
├── application.properties           # DB, App configurations
├── static/                          # EMPTY — No frontend files yet
└── templates/                       # EMPTY — No templates yet
```

### 3.3. Test Code (`src/test/java/com/ivanovp/medical_record/`)
```
src/test/java/com/ivanovp/medical_record/
└── MedicalRecordApplicationTests.java  # Default Spring Boot test class
```

### 3.4. Root Level
```
medical-record/
└── medical-record/
    ├── build.gradle                 # Build configuration
    └── src/                         # Source code directory
```

## 4. Current Configuration Details

### 4.1. `build.gradle` Dependencies
| Dependency | Version/Details | Purpose |
|---|---|---|
| spring-boot-starter-data-jpa | 4.0.6 | ORM, Database access |
| spring-boot-starter-security | 4.0.6 | Authentication & Authorization |
| spring-boot-starter-webmvc | 4.0.6 | REST API, Web MVC |
| springdoc-openapi-starter-webmvc-ui | 3.0.2 | Swagger UI documentation |
| lombok | compileOnly | Boilerplate reduction |
| mssql-jdbc | runtimeOnly | MS SQL Server JDBC driver |

### 4.2. `application.properties` Configuration
```properties
spring.application.name=medical-record

spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MedicalRecordDB;encrypt=true;trustServerCertificate=true;
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=root
spring.datasource.password=123456

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Note:** The file ends with a startup command: `cd medical-record && .\gradlew bootRun`

## 5. What Needs to Be Implemented

To complete the project, the following components need to be created:

### 5.1. Database Layer
- **Entities:** User, Doctor, Patient, Specialty, Diagnosis, Examination, SickLeave
- **Repositories:** JpaRepository implementations for all entities
- **Database Schema:** MS SQL Server tables and relationships

### 5.2. Security Layer
- **JWT Token Provider:** Token generation and validation
- **CustomUserDetailsService:** User details loading for Spring Security
- **Security Configuration:** JWT filter, authentication manager, role-based access

### 5.3. Service Layer
- Service interfaces and implementations for:
  - Authentication
  - User management
  - Doctor management
  - Patient management
  - Examination management
  - Sick leave management
  - Statistics queries
  - Nomenclature (specialties, diagnoses)

### 5.4. Controller Layer (REST API)
- **AuthController:** Login, registration
- **DoctorController:** CRUD operations for doctors
- **PatientController:** Patient profiles, history, GP assignment
- **ExaminationController:** Medical examinations CRUD
- **SickLeaveController:** Sick leaves CRUD
- **StatisticsController:** Statistical queries
- **NomenclatureController:** Lookup data (specialties, diagnoses)

### 5.5. DTOs
- **Request DTOs:** UserRegisterDTO, UserLoginDTO, DoctorCreateDTO, PatientCreateDTO, ExaminationRequestDTO, SickLeaveRequestDTO, etc.
- **Response DTOs:** JwtAuthResponseDTO, PatientHistoryDTO, etc.

### 5.6. Exception Handling
- **GlobalExceptionHandler:** @ControllerAdvice for global error handling
- **ResourceNotFoundException:** Custom exception class

### 5.7. Frontend
- Static HTML pages for login, registration, doctor dashboard, patient dashboard
- CSS (Bootstrap) for styling
- JavaScript (Fetch API) for API communication

## 6. Architectural Rules (To Be Followed)

1. **Dependency Injection:** Constructor Injection only (`@RequiredArgsConstructor`)
2. **DTO Usage:** Never expose Entities directly in Controllers
3. **Validation:** Use `@Valid` on Controller inputs
4. **Error Handling:** Global `@ControllerAdvice` (`GlobalExceptionHandler`)
5. **Security:** Role-based access control (`ADMIN`, `DOCTOR`, `PATIENT`)
