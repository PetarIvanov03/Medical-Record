# Code Rules — medical-record

**Package:** `com.ivanovp.medical_record`
**Stack:** Java 25 · Spring Boot 4.0.6 · MS SQL Server · Spring Data JPA · Spring Security + JWT · Lombok
**Imports:** Always `jakarta.*` — never `javax.*`

## Rules (always apply)
1. **DI** — `@RequiredArgsConstructor` only. Never `@Autowired`.
2. **DTOs** — Controllers accept `dto/request/*`, return `dto/response/*`. Never expose JPA entities.
3. **Validation** — `@Valid` on controller method params. DTOs use `@NotBlank`, `@NotNull`, `@Size`, `@Pattern`.
4. **Errors** — Throw `ResourceNotFoundException` for missing resources. `GlobalExceptionHandler` (@ControllerAdvice) catches all.
5. **Security** — Three roles: `ROLE_ADMIN`, `ROLE_DOCTOR`, `ROLE_PATIENT`.
6. **Lombok** — `@Data`/`@Getter`/`@Setter` on DTOs. `@Getter`/`@Setter`/`@NoArgsConstructor` on entities. `@RequiredArgsConstructor` on services/controllers.
