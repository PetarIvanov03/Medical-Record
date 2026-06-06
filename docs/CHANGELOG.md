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
