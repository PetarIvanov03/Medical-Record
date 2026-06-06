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