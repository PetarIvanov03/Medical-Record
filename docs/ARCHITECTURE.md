# Architecture Reference — medical-record

## Tech Stack
| Component | Details |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.6 (Gradle) |
| Database | MS SQL Server (MSSQL JDBC, ddl-auto: update) |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT Bearer token |
| API Docs | SpringDoc OpenAPI 3.0.2 (Swagger UI) |
| Utilities | Lombok |

Main class: `com.ivanovp.medical_record.MedicalRecordApplication`

## Package Structure
```
com.ivanovp.medical_record/
├── config/        SecurityConfig, OpenApiConfig
├── controller/    AuthController, DoctorController, PatientController,
│                  ExaminationController, SickLeaveController,
│                  StatisticsController, NomenclatureController
├── dto/
│   ├── request/   *CreateDTO, *UpdateDTO, *RequestDTO, UserLoginDTO, ChangeGpDTO
│   └── response/  *ResponseDTO, JwtAuthResponseDTO, StatCountDTO, RevenueDTO
├── entity/        User, Doctor, Patient, Specialty, Diagnosis, Examination, SickLeave
├── exception/     GlobalExceptionHandler (@ControllerAdvice), ResourceNotFoundException
├── repository/    Spring Data JPA interfaces + custom JPQL
├── security/      JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService
└── service/
    ├── *Service.java         interfaces
    └── impl/*ServiceImpl.java  implementations
```

## Entity Definitions

### UserRole (enum)
Values: `ADMIN`, `DOCTOR`, `PATIENT`

### User
`id` · `username` (unique) · `password` (BCrypt) · `role` (UserRole)

### Specialty
`id` · `name` (unique)
Relations: `@ManyToMany` ↔ Diagnosis (join table: `specialty_diagnoses`)

### Diagnosis
`id` · `code` (unique, ICD) · `name`
Relations: `@ManyToMany` ↔ Specialty (mappedBy = "diagnoses")

### Doctor
`id` · `uin` (unique, 10 digits) · `name` · `isGp` (boolean, default false)
Relations: `@ManyToOne` → Specialty · `@OneToOne` (optional) → User

### Patient
`id` · `egn` (unique, 10 digits) · `name` · `isInsured` (boolean, default true)
Relations: `@ManyToOne` (optional) → Doctor (gp) · `@OneToOne` (optional) → User

### Examination
`id` · `examDate` (LocalDateTime) · `treatment` (String, nullable) · `price` (BigDecimal) · `paidByNzok` (boolean)
Relations: `@ManyToOne` → Doctor · `@ManyToOne` → Patient · `@ManyToOne` (optional) → Diagnosis
**Business rule:** `paidByNzok = patient.isInsured` — set automatically on creation

### SickLeave
`id` · `startDate` (LocalDate) · `durationDays` (int)
Relations: `@OneToOne` → Examination (unique FK)

## Database Relationships
```
users      1:1  doctors       (doctors.user_id)
users      1:1  patients      (patients.user_id)
specialties 1:N doctors       (doctors.specialty_id)
doctors    1:N  patients      (patients.gp_id)  — "is GP of"
doctors    1:N  examinations  (examinations.doctor_id)
patients   1:N  examinations  (examinations.patient_id)
diagnoses  1:N  examinations  (examinations.diagnosis_id, nullable)
examinations 1:1 sick_leaves  (sick_leaves.examination_id UNIQUE)
specialties M:N diagnoses     (specialty_diagnoses junction table)
```

## Key Business Rules
1. `paidByNzok` is auto-set from `patient.isInsured` when creating an examination
2. A doctor may only update/delete their own examinations
3. A patient may only view their own examination history
4. One examination → at most one sick leave (1:1 constraint)
5. `specialty_diagnoses` restricts which diagnoses a specialty's doctors may assign
6. EGN = exactly 10 digits; UIN = exactly 10 digits

## API Route Groups
| Prefix | Roles | Controller |
|---|---|---|
| `/api/auth/**` | ALL (no auth) | AuthController |
| `GET /api/doctors/**` | ADMIN, DOCTOR, PATIENT | DoctorController |
| `/api/admin/doctors/**` | ADMIN | DoctorController |
| `GET /api/patients/**` | ADMIN, DOCTOR | PatientController |
| `/api/patients/my-**` | PATIENT | PatientController |
| `/api/admin/patients/**` | ADMIN | PatientController |
| `/api/examinations/**` | DOCTOR / ADMIN (varies) | ExaminationController |
| `/api/admin/examinations/**` | ADMIN | ExaminationController |
| `/api/sick-leaves/**` | DOCTOR / ADMIN (varies) | SickLeaveController |
| `/api/statistics/**` | ADMIN, DOCTOR | StatisticsController |
| `GET /api/specialties` | ADMIN, DOCTOR | NomenclatureController |
| `GET /api/diagnoses` | DOCTOR | NomenclatureController |
| `/api/admin/specialties/**` | ADMIN | NomenclatureController |
| `/api/admin/diagnoses/**` | ADMIN | NomenclatureController |

## application.properties (required keys)
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MedicalRecordDB;...
spring.datasource.username=root
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=update

app.jwt.secret=<256-bit-base64-secret>
app.jwt.expiration=86400000
```
