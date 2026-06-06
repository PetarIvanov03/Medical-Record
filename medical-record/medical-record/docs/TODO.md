# Implementation Plan — medical-record

> One checkbox = one Qwen prompt.
> Each step targets a single file or a tight group of related files.
> Include `PROMPT_CONTEXT.md` in every prompt. Add relevant ARCHITECTURE.md sections as needed.

---

## Phase 1 — JPA Entities

- [ ] **1.1** `entity/UserRole.java` — enum: ADMIN, DOCTOR, PATIENT
- [ ] **1.2** `entity/User.java` — id, username (unique), password, role (UserRole)
- [ ] **1.3** `entity/Specialty.java` — id, name (unique); `@ManyToMany` List\<Diagnosis\> via join table `specialty_diagnoses`
- [ ] **1.4** `entity/Diagnosis.java` — id, code (unique), name; `@ManyToMany(mappedBy="diagnoses")` List\<Specialty\>
- [ ] **1.5** `entity/Doctor.java` — id, uin (unique), name, isGp; `@ManyToOne` Specialty; `@OneToOne(optional)` User
- [ ] **1.6** `entity/Patient.java` — id, egn (unique), name, isInsured; `@ManyToOne(optional)` Doctor gp; `@OneToOne(optional)` User
- [ ] **1.7** `entity/Examination.java` — id, examDate (LocalDateTime), treatment, price (BigDecimal), paidByNzok; `@ManyToOne` Doctor, Patient; `@ManyToOne(optional)` Diagnosis
- [ ] **1.8** `entity/SickLeave.java` — id, startDate (LocalDate), durationDays; `@OneToOne` Examination (unique column)

## Phase 2 — Repositories

- [ ] **2.1** `repository/UserRepository.java`
      `Optional<User> findByUsername(String username)`
- [ ] **2.2** `repository/SpecialtyRepository.java`
      `boolean existsByName(String name)`
- [ ] **2.3** `repository/DiagnosisRepository.java`
      `boolean existsByCode(String code)`
- [ ] **2.4** `repository/DoctorRepository.java`
      `List<Doctor> findByIsGpTrue()`
      `Optional<Doctor> findByUserId(Long userId)`
- [ ] **2.5** `repository/PatientRepository.java`
      `Optional<Patient> findByUserId(Long userId)`
      `List<Patient> findByGpId(Long gpId)`
- [ ] **2.6** `repository/ExaminationRepository.java`
      `List<Examination> findByDoctorId(Long doctorId)`
      `List<Examination> findByPatientId(Long patientId)`
      `List<Examination> findByExamDateBetween(LocalDateTime from, LocalDateTime to)`
- [ ] **2.7** `repository/SickLeaveRepository.java`
      `Optional<SickLeave> findByExaminationId(Long examinationId)`
      `boolean existsByExaminationId(Long examinationId)`

## Phase 3 — Exception Handling

- [ ] **3.1** `exception/ResourceNotFoundException.java`
      extends RuntimeException; constructor: `(String message)`
- [ ] **3.2** `exception/GlobalExceptionHandler.java`
      `@ControllerAdvice`; handle:
      - ResourceNotFoundException → 404
      - MethodArgumentNotValidException → 400 with field errors
      - AccessDeniedException → 403
      - generic Exception → 500

## Phase 4 — Security

- [ ] **4.1** `security/JwtTokenProvider.java`
      `String generateToken(UserDetails user)`
      `boolean validateToken(String token)`
      `String getUsernameFromToken(String token)`
      Read secret/expiration from `app.jwt.secret` / `app.jwt.expiration`
- [ ] **4.2** `security/JwtAuthenticationFilter.java`
      extends OncePerRequestFilter; extract `Bearer` token from header; validate; set SecurityContextHolder
- [ ] **4.3** `security/CustomUserDetailsService.java`
      implements UserDetailsService; `loadUserByUsername` via UserRepository; map UserRole to `GrantedAuthority`
- [ ] **4.4** `config/SecurityConfig.java`
      `@Bean SecurityFilterChain` — permit `/api/auth/**`; role-based rules per ARCHITECTURE route groups
      `@Bean BCryptPasswordEncoder`
      `@Bean AuthenticationManager`
      Add JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter

## Phase 5 — Auth Module

- [ ] **5.1** Three DTOs:
      `dto/request/UserRegisterDTO.java` — username, password, name, egn (10 digits @Pattern)
      `dto/request/UserLoginDTO.java` — username, password
      `dto/response/JwtAuthResponseDTO.java` — token, username, role
- [ ] **5.2** `service/AuthService.java` + `service/impl/AuthServiceImpl.java`
      `JwtAuthResponseDTO register(UserRegisterDTO dto)` — creates User + Patient
      `JwtAuthResponseDTO login(UserLoginDTO dto)` — authenticates, returns JWT
- [ ] **5.3** `controller/AuthController.java`
      `POST /api/auth/register` [ALL]
      `POST /api/auth/login` [ALL]

## Phase 6 — Nomenclature Module

- [ ] **6.1** Four DTOs:
      `dto/request/SpecialtyRequestDTO.java` — name (@NotBlank)
      `dto/response/SpecialtyResponseDTO.java` — id, name
      `dto/request/DiagnosisRequestDTO.java` — code (@NotBlank), name (@NotBlank)
      `dto/response/DiagnosisResponseDTO.java` — id, code, name
- [ ] **6.2** `service/NomenclatureService.java` + `service/impl/NomenclatureServiceImpl.java`
      getAllSpecialties, getAllDiagnoses
      createSpecialty, updateSpecialty, deleteSpecialty
      createDiagnosis, updateDiagnosis, deleteDiagnosis
- [ ] **6.3** `controller/NomenclatureController.java`
      `GET /api/specialties` [ADMIN, DOCTOR]
      `GET /api/diagnoses` [DOCTOR]
      `POST /api/admin/specialties` [ADMIN]
      `PUT /api/admin/specialties/{id}` [ADMIN]
      `DELETE /api/admin/specialties/{id}` [ADMIN]
      `POST /api/admin/diagnoses` [ADMIN]
      `PUT /api/admin/diagnoses/{id}` [ADMIN]
      `DELETE /api/admin/diagnoses/{id}` [ADMIN]

## Phase 7 — Doctor Module

- [ ] **7.1** Three DTOs:
      `dto/request/DoctorCreateDTO.java` — uin (10 digits), name, specialtyId, isGp, username, password
      `dto/request/DoctorUpdateDTO.java` — name, specialtyId, isGp
      `dto/response/DoctorResponseDTO.java` — id, uin, name, specialtyName, isGp
- [ ] **7.2** `service/DoctorService.java` + `service/impl/DoctorServiceImpl.java`
      getAllDoctors, getDoctorById, createDoctor (also creates linked User), updateDoctor, deleteDoctor
- [ ] **7.3** `controller/DoctorController.java`
      `GET /api/doctors` [ALL]
      `GET /api/doctors/{id}` [ALL]
      `POST /api/admin/doctors` [ADMIN]
      `PUT /api/admin/doctors/{id}` [ADMIN]
      `DELETE /api/admin/doctors/{id}` [ADMIN]

## Phase 8 — Patient Module

- [ ] **8.1** Four DTOs:
      `dto/request/PatientCreateDTO.java` — username, password, name, egn, isInsured, gpId (nullable)
      `dto/request/ChangeGpDTO.java` — gpId (@NotNull)
      `dto/response/PatientResponseDTO.java` — id, name, egn, isInsured, gpName
      `dto/response/PatientHistoryDTO.java` — PatientResponseDTO fields + List\<ExaminationResponseDTO\>
- [ ] **8.2** `service/PatientService.java` + `service/impl/PatientServiceImpl.java`
      getAllPatients, getPatientById, getMyProfile(principal), changeGp
      getPatientHistory(id), getMyHistory(principal)
      createPatient (admin), deletePatient (admin)
- [ ] **8.3** `controller/PatientController.java`
      `GET /api/patients` [ADMIN, DOCTOR]
      `GET /api/patients/{id}` [ADMIN, DOCTOR]
      `GET /api/patients/{id}/history` [ADMIN, DOCTOR]
      `GET /api/patients/my-profile` [PATIENT]
      `GET /api/patients/my-history` [PATIENT]
      `PUT /api/patients/my-profile/change-gp` [PATIENT]
      `POST /api/admin/patients` [ADMIN]
      `DELETE /api/admin/patients/{id}` [ADMIN]

## Phase 9 — Examination Module

- [ ] **9.1** Two DTOs:
      `dto/request/ExaminationRequestDTO.java` — patientId, diagnosisId (nullable), treatment (nullable), price (@NotNull, @Positive)
      `dto/response/ExaminationResponseDTO.java` — id, examDate, doctorName, patientName, diagnosisCode, diagnosisName, treatment, price, paidByNzok
- [ ] **9.2** `service/ExaminationService.java` + `service/impl/ExaminationServiceImpl.java`
      `createExamination(dto, principal)` — auto-set paidByNzok = patient.isInsured; set examDate = now()
      `updateExamination(id, dto, principal)` — verify principal is owner doctor
      `deleteExamination(id)` — admin only (called from service by controller)
      getById, getAllForAdmin, getMyHistory(doctorPrincipal)
- [ ] **9.3** `controller/ExaminationController.java`
      `GET /api/examinations` [ADMIN]
      `GET /api/examinations/my-history` [DOCTOR]
      `GET /api/examinations/{id}` [ADMIN, DOCTOR, PATIENT]
      `POST /api/examinations` [DOCTOR]
      `PUT /api/examinations/{id}` [DOCTOR]
      `DELETE /api/admin/examinations/{id}` [ADMIN]

## Phase 10 — Sick Leave Module

- [ ] **10.1** Two DTOs:
       `dto/request/SickLeaveRequestDTO.java` — examinationId, startDate (@NotNull), durationDays (@Min(1))
       `dto/response/SickLeaveResponseDTO.java` — id, startDate, durationDays, examinationId, patientName, doctorName
- [ ] **10.2** `service/SickLeaveService.java` + `service/impl/SickLeaveServiceImpl.java`
       `createSickLeave(dto, principal)` — check `existsByExaminationId` (409 if already exists); check doctor owns examination
       updateSickLeave — verify doctor ownership
       deleteSickLeave — ADMIN or owning doctor
- [ ] **10.3** `controller/SickLeaveController.java`
       `GET /api/sick-leaves/{id}` [ADMIN, DOCTOR, PATIENT]
       `POST /api/sick-leaves` [DOCTOR]
       `PUT /api/sick-leaves/{id}` [DOCTOR]
       `DELETE /api/sick-leaves/{id}` [ADMIN, DOCTOR]

## Phase 11 — Statistics Module

- [ ] **11.1** Add JPQL to `ExaminationRepository.java`:
       `@Query` findPatientsByDiagnosisId(Long diagnosisId) — returns List\<Patient\>
       `@Query` findMostCommonDiagnosis() — returns Diagnosis (ORDER BY count DESC, LIMIT 1)
       `@Query` findRevenueTotal() — SUM(price) WHERE paidByNzok = false
       `@Query` findRevenueByDoctor() — List of [doctorName, sum] grouped by doctor
       `@Query` countVisitsPerDoctor() — List of [doctorName, count]
- [ ] **11.2** Add JPQL to `PatientRepository.java`:
       `@Query` countPatientsByGp() — List of [gpName, count]
- [ ] **11.3** Add JPQL to `SickLeaveRepository.java`:
       `@Query` findPeakSickLeaveMonth() — returns month number with highest count
       `@Query` findDoctorsWithMostSickLeaves() — returns List\<Doctor\> (DESC by count)
- [ ] **11.4** Two result DTOs:
       `dto/response/StatCountDTO.java` — label (String), count (Long)
       `dto/response/RevenueDTO.java` — label (String), revenue (BigDecimal)
- [ ] **11.5** `service/StatisticsService.java` + `service/impl/StatisticsServiceImpl.java`
- [ ] **11.6** `controller/StatisticsController.java`
       All `GET /api/statistics/*` endpoints from spec (see api_endpoints_specification.txt)

## Phase 12 — OpenAPI Config

- [ ] **12.1** `config/OpenApiConfig.java`
       `@Bean OpenAPI` with `SecurityScheme` for HTTP Bearer JWT
       `@Bean GroupedOpenApi` for all /api/** paths

## Phase 13 — application.properties

- [ ] **13.1** Add JWT keys to `src/main/resources/application.properties`:
       `app.jwt.secret` (min 32 chars, base64)
       `app.jwt.expiration=86400000` (24 hours in ms)

## Phase 14 — Frontend (Static HTML + Vanilla JS)

- [ ] **14.1** `static/js/auth.js`
       `login()` — POST /api/auth/login, store JWT + role in localStorage, redirect by role
       `register()` — POST /api/auth/register
       `logout()` — clear localStorage, redirect to index.html
- [ ] **14.2** `static/index.html` — login form (username, password); links auth.js
- [ ] **14.3** `static/register.html` — registration form (username, password, name, EGN)
- [ ] **14.4** `static/js/nomenclature.js`
       `loadSpecialties(selectId)`, `loadDiagnoses(selectId)` — populate \<select\> dropdowns
- [ ] **14.5** `static/dashboard-patient.html` + `static/js/patient.js`
       Show my history table; change GP form with doctor dropdown
- [ ] **14.6** `static/dashboard-doctor.html` + `static/js/examination.js`
       List my examinations; create/edit examination modal (patient search, diagnosis dropdown, price, treatment)
       Sick leave issuing form linked to examination
- [ ] **14.7** `static/dashboard-admin.html` + `static/js/statistics.js`
       Tabs: Doctors CRUD, Patients list, Diagnoses CRUD, Statistics panel
       Statistics: tables/cards for all /api/statistics/* endpoints

## Phase 15 — Tests

- [ ] **15.1** `test/.../service/ExaminationServiceTest.java`
       Mock UserRepository, ExaminationRepository, PatientRepository
       Test: paidByNzok = true when patient.isInsured = true
       Test: paidByNzok = false when patient.isInsured = false
       Test: update throws exception when doctor is not owner
- [ ] **15.2** `test/.../repository/ExaminationRepositoryTest.java`
       `@DataJpaTest` with H2 in-memory (or MSSQL TestContainers)
       Test each custom JPQL query in ExaminationRepository
- [ ] **15.3** `test/.../controller/DoctorControllerTest.java`
       `@WebMvcTest(DoctorController.class)` + `@MockBean DoctorService`
       Test: GET /api/doctors returns 200 without auth
       Test: POST /api/admin/doctors returns 403 for DOCTOR role
       Test: POST /api/admin/doctors returns 201 for ADMIN role

---

## Dependency Order
```
Phase 1 (entities)
  → Phase 2 (repositories)
    → Phase 3 (exceptions)
      → Phase 4 (security)
        → Phase 5 (auth)
          → Phases 6–10 (modules, any order)
            → Phase 11 (statistics — needs all repos)
              → Phase 12–13 (config/properties)
                → Phase 14 (frontend)
                  → Phase 15 (tests)
```
