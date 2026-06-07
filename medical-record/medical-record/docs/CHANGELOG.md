# Changelog — medical-record

All project changes are recorded here chronologically.

---

## [2026-06-06] — Planning phase complete

### Added
- Final database schema designed for MS SQL Server with corrected foreign key relations.
- Full REST API endpoint specification (AuthController, DoctorController, PatientController,
  ExaminationController, SickLeaveController, StatisticsController, NomenclatureController).
- Admin CRUD endpoints added for Diagnoses and Specialties (required by assignment).
- Backend package structure defined (controller / service / dto / entity / repository / security / exception).
- Static frontend structure defined (HTML + Bootstrap + Vanilla JS).
- `docs/` folder created with AI context files (ARCHITECTURE, PROMPT_CONTEXT, TODO, CHANGELOG).
- All docs translated to English and updated to correct tech stack (Java 25, Spring Boot 4.0.6).

### Fixed
- PROMPT_CONTEXT.md corrected: Java 21 → Java 25, Spring Boot 3.x → Spring Boot 4.0.6.
- SQL schema note: `boolean` columns should be `BIT` in MS SQL Server;
  Hibernate handles DDL correctly regardless.

## [2026-06-06] — Phase 1 complete
### Added
- All 7 JPA entities created: UserRole (enum), User, Specialty, Diagnosis, Doctor, Patient, Examination, SickLeave.
- M:N relation Specialty ↔ Diagnosis via specialty_diagnoses join table.
- All foreign key relations mapped with correct fetch strategies.

## [2026-06-06] — Phase 2 complete
### Added
- All 7 repositories created: UserRepository, SpecialtyRepository, DiagnosisRepository,
  DoctorRepository, PatientRepository, ExaminationRepository, SickLeaveRepository.
- Custom query methods for user lookup, GP filtering, and date-range examination search.

## [2026-06-06] — Phase 3 complete
### Added
- ResourceNotFoundException (@ResponseStatus 404)
- GlobalExceptionHandler (@RestControllerAdvice) — handles 404, 400, 403, 500

## [2026-06-06] — Phase 4 complete
### Added
- JwtTokenProvider (JJWT 0.12.x) — generate, validate, extract username
- JwtAuthenticationFilter (OncePerRequestFilter) — Bearer token extraction
- CustomUserDetailsService — loads user + maps role to GrantedAuthority
- SecurityConfig — stateless JWT, permit /api/auth/**, BCrypt bean

## [2026-06-06] — Phase 5 complete
### Added
- UserRegisterDTO, UserLoginDTO, JwtAuthResponseDTO
- AuthService interface + AuthServiceImpl (register creates User+Patient, login returns JWT)
- AuthController — POST /api/auth/register (201), POST /api/auth/login (200)

## [2026-06-06] — Phase 6 complete
### Added
- SpecialtyRequestDTO, SpecialtyResponseDTO, DiagnosisRequestDTO, DiagnosisResponseDTO
- NomenclatureService + NomenclatureServiceImpl (full CRUD for specialties and diagnoses)
- NomenclatureController — 8 endpoints (/api/specialties, /api/diagnoses, /api/admin/*)

## [2026-06-06] — Phase 7 complete
### Added
- DoctorCreateDTO, DoctorUpdateDTO, DoctorResponseDTO
- DoctorService + DoctorServiceImpl (creates linked User+Doctor, full CRUD)
- DoctorController — 5 endpoints (/api/doctors, /api/admin/doctors/*)

## [2026-06-06] — Phase 8 complete
### Added
- PatientCreateDTO, ChangeGpDTO, PatientResponseDTO, PatientHistoryDTO, ExaminationResponseDTO (placeholder)
- PatientService + PatientServiceImpl (getMyProfile, changeGp validates isGp=true, history returns empty list for now)
- PatientController — 8 endpoints

## [2026-06-06] — Phase 9 complete
### Added
- ExaminationRequestDTO, ExaminationResponseDTO (full version replacing placeholder)
- ExaminationService + ExaminationServiceImpl (NZOK auto-calc, specialty-diagnosis validation, doctor ownership check)
- ExaminationController — 6 endpoints

## [2026-06-06] — Phase 10 complete
### Added
- SickLeaveRequestDTO, SickLeaveResponseDTO
- SickLeaveService + SickLeaveServiceImpl (1:1 duplicate check, doctor ownership)
- SickLeaveController — 4 endpoints at /api/sick-leaves

## [2026-06-06] — Phase 11 complete
### Added
- StatCountDTO, RevenueDTO
- StatisticsService + StatisticsServiceImpl (10 статистически метода)
- StatisticsController — 10 endpoints at /api/statistics/*
- JPQL queries added to ExaminationRepository, PatientRepository, SickLeaveRepository

## [2026-06-06] — Phase 12 complete
### Added
- OpenApiConfig — Swagger UI with JWT Bearer security scheme

## [2026-06-06] — Phase 13 complete
### Added
- UserResponseDTO, UserService, UserServiceImpl, AdminUserController
- GET /api/admin/users, DELETE /api/admin/users/{id}

## [2026-06-07] — Audit fixes (Phases 1–7)
### Fixed
**Phase 1 — Compilation errors**
- `StatisticsServiceImpl.getMostCommonDiagnosis()`: added missing 4th `specialtyName` argument to `DiagnosisResponseDTO` constructor, derived from `diagnosis.getSpecialties()` (same pattern as `NomenclatureServiceImpl`).
- `DoctorControllerTest`: replaced non-existent `dto.setIsGp(false)` with Lombok-generated `dto.setGp(false)`.

**Phase 2 — Test infrastructure**
- Created `src/test/resources/application.properties` with H2 in-memory datasource so `@SpringBootTest` context loads without MS SQL Server.
- Removed MS SQL-specific `columnDefinition = "BIT DEFAULT 1/0"` from `Patient.isInsured` and `Doctor.isGp` entities; Java field initializers already guarantee the same defaults in a JPA-portable way.
- Created `TestSecurityConfig` (`@TestConfiguration @EnableMethodSecurity`) with a stateless filter chain returning 401 for unauthenticated requests; imported it in `DoctorControllerTest` so `@PreAuthorize` is enforced under `@WithMockUser`.

**Phase 3 — Missing role checks**
- Added `@PreAuthorize` to all endpoints in `AdminUserController`, `PatientController`, `ExaminationController`, `SickLeaveController`, and `StatisticsController` per `api_endpoints_specification.txt`.
- Added defense-in-depth `.requestMatchers("/api/admin/**").hasRole("ADMIN")` to `SecurityConfig`.

**Phase 4 — LazyInitializationException prevention**
- Added `@Transactional(readOnly = true)` at class level in `StatisticsServiceImpl`.
- Added `@Transactional(readOnly = true)` to `NomenclatureServiceImpl.getAllDiagnoses()` and `getAllSpecialties()`.

**Phase 5 — Exception handling**
- `ExaminationServiceImpl.updateExamination` now throws `AccessDeniedException` (403) instead of `IllegalArgumentException` (500) for ownership violations.
- `SickLeaveServiceImpl.createSickLeave` and `updateSickLeave` ownership branches now throw `AccessDeniedException`.
- `ExaminationServiceTest.updateExamination_whenNotOwner_*` updated to expect `AccessDeniedException`; added missing `examinationRepository.findById` mock.
- `GlobalExceptionHandler`: added `IllegalArgumentException` → 400, `AuthenticationException` → 401; fixed catch-all `Exception` handler to return generic "Internal server error" message and log the exception.
- `AuthServiceImpl.login`: replaced bare `.orElseThrow()` with `BadCredentialsException("Invalid credentials")`.

**Phase 6 — Business logic**
- `SickLeaveService.deleteSickLeave` signature changed to `(Long id, String username, boolean isAdmin)`.
- `SickLeaveServiceImpl.deleteSickLeave` enforces ownership: ADMIN may delete any; DOCTOR may only delete a sick leave whose examination they own (throws `AccessDeniedException` otherwise).
- `SickLeaveController.deleteSickLeave` now extracts caller username and admin flag from `Authentication` and passes them to the service.

**Phase 7 — Validation / DTO nits**
- `SickLeaveRequestDTO.durationDays` changed from primitive `int` to `Integer` (keeping `@NotNull @Min(1)`).
- Added `@JsonProperty("isInsured")` to `PatientResponseDTO.isInsured` and `PatientCreateDTO.isInsured` so the JSON key matches what the frontend JS reads (`data.isInsured`).
- Added `@JsonProperty("isGp")` to `DoctorResponseDTO.isGp` and `DoctorCreateDTO.isGp` so the JSON key matches the documented field name used in request bodies.

## [2026-06-06] — Phase 14 complete
### Added
- auth.js, nomenclature.js, examination.js, doctor.js, patient.js, statistics.js
- index.html, register.html, dashboard-patient.html, dashboard-doctor.html, dashboard-admin.html
- Full Bootstrap 5 frontend connected to REST API via Fetch + JWT
### Fixed
- Corrected API paths for admin endpoints in dashboard-admin.html
- Fixed DTO field name mappings in examination.js and patient.js
- Fixed statistics.js peak month and revenue display