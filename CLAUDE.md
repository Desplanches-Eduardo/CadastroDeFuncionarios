# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Authorship

**The backend in this repository was written by hand by its author, without help from Claude or any other AI tool.** Every Java class, the Flyway migration, `application.properties`, `pom.xml` and the README were written by the author. Claude's only contribution to this repository is this `CLAUDE.md` file.

Claude wrote the frontend, which lives in a separate repository (`~/projetos/cadastro-funcionarios-web`) and talks to this API without any backend change: it works around the API's quirks (no CORS, text bodies on `POST`, `500` on duplicate email) on its own side.

Do not change backend code (Java, migrations, config, `pom.xml`) unless the author explicitly asks for it. When a backend change looks useful, describe it and let the author decide and write it.

## Project

Spring Boot 4.1 REST API (Java 21, Maven wrapper) for registering employees (`funcionarios`) and tasks (`tarefas`). It is a port of the author's earlier "ninjas" project to a new domain. The frontend is a separate React project (see Authorship). There are no tests beyond the generated `contextLoads`. Code, messages, README and commit messages are in Portuguese; commits use Conventional Commit prefixes (`feat:`, `fix:`, `docs:`, `chore:`) followed by a capitalized Portuguese sentence.

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

Package-by-feature under `desplanches.CadastroDeFuncionarios`: `funcionarios/` and `tarefas/` each contain the same six classes — `Model` (JPA entity), `DTO`, `Mapper` (hand-written `@Component`, two overloaded `map` methods), `Repository` (`JpaRepository`), `Service`, `Controller`. The two features mirror each other; a change to one usually belongs in the other too.

- **Routes**: both controllers expose the same five verb-named paths: `POST /criar`, `GET /listar`, `GET /listar/{id}`, `PUT /alterar/{id}`, `DELETE /deletar/{id}`, under `/funcionarios` and `/tarefas`.
- **Not-found convention**: services return `null` when an id doesn't exist, and controllers turn that into a `404` with a Portuguese message body (hence `ResponseEntity<?>`). There is no exception handler / `@ControllerAdvice`, so constraint violations (duplicate email, missing FK, id sent on create) surface as `500`.
- **PUT replaces the whole entity**: the service maps the DTO to a new entity, sets the path id, and saves. Fields missing from the body become null.
- **Relationship**: `FuncionariosModel.tarefas` is `@ManyToOne` (FK `tarefa_id`); `TarefasModel.funcionarios` is `@OneToMany` with `@JsonIgnore` to break the serialization cycle. The DTOs reference entity types directly (`FuncionariosDTO.tarefas` is a `TarefasModel`, `TarefasDTO.funcionarios` is a `List<FuncionariosModel>`), so creating a funcionario takes `"tarefas": {"id": 1}` and the task must already exist. `TarefasDTO.funcionarios` has no `@JsonIgnore`, so `GET /tarefas/listar` includes each task's employees.
- **Task delete**: `TarefasController.deletarTarefa` checks for linked funcionarios itself and returns `409` before calling the service, instead of letting the FK violation become a `500`.
- **Swagger docs** live as `@Operation` / `@ApiResponses` / `@Parameter` annotations on each controller method (springdoc); there is no separate OpenAPI file. Keep annotations in sync when changing a route.

## Database

- Schema is owned by Flyway; migrations live in `src/main/resources/db/migrations` (note: `migrations`, plural, configured in `application.properties`). `V1__Create_tables.sql` creates `tb_tarefas` and `tb_cadastro`.
- `spring.jpa.hibernate.ddl-auto=validate`: Hibernate never changes the schema. Any entity field change needs a new `V<n>__*.sql` migration, or startup fails validation. Don't edit `V1`.
- The image column is `url_img` in DB, entity and DTO (deliberately, not `imgUrl` as in the ninjas project) because the frontend consumes that DTO shape.
- `idade` is a primitive `int` in the DTO; with Spring Boot 4's Jackson a request body missing it is rejected with `400`.
