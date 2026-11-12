# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Authorship

**The backend in this repository was written by hand by its author.** The first version (up to commit `64314d1`) had no help from Claude or any other AI tool. The record refactor that followed (branch `refactor/dto-record`) was typed by the author, following a step-by-step review with Claude that included code snippets; Claude then split it into commits, opened the PR and updated the README and this file.

Claude wrote the frontend, which lives in a separate repository (`~/projetos/cadastro-funcionarios-web`). It was written against the first version of the API and works around its quirks (no CORS, text bodies on `POST`, `500` on duplicate email) on its own side; the record refactor changed the JSON contract, so the frontend has to follow it (see Architecture).

Do not change backend code (Java, migrations, config, `pom.xml`) unless the author explicitly asks for it. When a backend change looks useful, describe it and let the author decide and write it.

## Project

Spring Boot 4.1 REST API (Java 21, Maven wrapper) for registering employees (`funcionarios`) and tasks (`tarefas`). It is a port of the author's earlier "ninjas" project to a new domain. The frontend is a separate React project (see Authorship). There are no tests beyond the generated `contextLoads`, and no input validation yet (`@Valid` is the planned next step). Code, messages, README and commit messages are in Portuguese; commits use Conventional Commit prefixes (`feat:`, `fix:`, `docs:`, `chore:`) followed by a capitalized Portuguese sentence.

## Commands

The datasource comes **only** from environment variables with no defaults. Without them the app and the tests fail with `Driver org.h2.Driver claims to not accept jdbcUrl, ${DATABASE_URL}`:

```bash
export DATABASE_URL='jdbc:h2:mem:testdb'
export DATABASE_USERNAME=sa
export DATABASE_PASSWORD=''

./mvnw spring-boot:run                 # port 8080
./mvnw test                            # all tests (needs the same env vars)
./mvnw test -Dtest=ClassName#method    # single test
./mvnw clean package
```

With the app running: Swagger UI at `/swagger-ui/index.html`, OpenAPI JSON at `/v3/api-docs`, H2 console at `/h2-console`.

The configuration also accepts, without code changes, a file database (`DATABASE_URL='jdbc:h2:file:<path>;AUTO_SERVER=TRUE'`, which keeps data between runs) and another port (`SERVER_PORT=8081`). The frontend's `scripts/backend.sh` uses the file database in `~/projetos/dados-cadastro-funcionarios/`. The frontend's own tests are Vitest against a fake in-memory API (MSW), so they never touch that data and don't need this backend running at all.

## Architecture

Package-by-feature under `desplanches.CadastroDeFuncionarios`: `funcionarios/` and `tarefas/` each contain `Model` (JPA entity, Lombok `@Getter`/`@Setter` — not `@Data`, whose `equals`/`hashCode` loop through the bidirectional relationship), `DTO`, `Repository`, `Service`, `Controller`. The two features mirror each other; a change to one usually belongs in the other too. `common/` holds the shared exceptions and the handler.

- **DTOs are records** with static `fromEntity` and `toEntity`; there are no mapper classes. `FuncionariosDTO` is `(id, nome, email, urlImg, cargo, idade, tarefasId)` — the task travels as its id, never as an entity — and `toEntity(dto, tarefa)` receives the task already loaded by the service. `TarefasDTO` is `(id, nome, dificuldade)`, without the employee list. `toEntity` never sets the id, so an id sent on `POST` is ignored.
- **Errors**: services throw `RecursoNaoEncontradoException` (→ `404`) or `RegraDeNegocioException` (→ `409`), both unchecked, mapped to a plain-text body by `common/GlobalExceptionHandler` (`@RestControllerAdvice`). Services never return `null`; each has a private `buscarOuFalhar(id)` helper. Controllers have no status `if`s and return typed `ResponseEntity<DTO>` (`201` on create, `204` with `ResponseEntity<Void>` on delete).
- **Business rules live in the service** (the author's rule: the backend decides, the frontend only shows): unknown `tarefasId` → `404`; duplicate email → `409` (`existsByEmail` on create, `existsByEmailAndIdNot` on update); deleting a task that still has employees → `409` (`existsByTarefasId`). The DB constraints (`unique`, FK) stay as the last barrier.
- **Update**: `alterar*` loads the existing entity, sets every field from the DTO and relies on `@Transactional` dirty checking (no `save`). It is a full replacement, so fields missing from the body become null; `idade` is a primitive `int`, so a body without it is `400`.
- **Routes**: both controllers expose the same five verb-named paths: `POST /criar`, `GET /listar`, `GET /listar/{id}`, `PUT /alterar/{id}`, `DELETE /deletar/{id}`, under `/funcionarios` and `/tarefas`.
- **Swagger docs** live as `@Operation` / `@ApiResponses` / `@Parameter` annotations on each controller method (springdoc); there is no separate OpenAPI file. Keep annotations in sync when changing a route or a status.

## Database

- Schema is owned by Flyway; migrations live in `src/main/resources/db/migrations` (note: `migrations`, plural, configured in `application.properties`). `V1__Create_tables.sql` creates `tb_tarefas` and `tb_cadastro`.
- `spring.jpa.hibernate.ddl-auto=validate`: Hibernate never changes the schema. Any entity field change needs a new `V<n>__*.sql` migration, or startup fails validation. Don't edit `V1`.
- The image is `urlImg` in Java and JSON, mapped to the `url_img` column with `@Column`, so the rename needed no migration.
- `idade` is a primitive `int` in the DTO; with Spring Boot 4's Jackson a request body missing it is rejected with `400`.
