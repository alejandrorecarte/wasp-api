Pue# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

- **Run the app:** `set -a && source .env && set +a && ./mvnw spring-boot:run`
- **Build:** `./mvnw clean package`
- **Run tests:** `set -a && source .env && set +a && ./mvnw test`
- **Run a single test class:** `./mvnw test -Dtest=ClassName`
- **Run a single test method:** `./mvnw test -Dtest=ClassName#methodName`
- **Format code:** `./mvnw spotless:apply` (Google Java Format, also runs automatically during `validate` phase)
- **Check style:** `./mvnw checkstyle:check` (Google Checks, also runs during `verify` phase)
- **Docker (local stack with PostgreSQL + PgAdmin):** `docker compose up`

The app runs on port 8080. Swagger UI is available at `/swagger-ui/`.

## Architecture

Spring Boot 2.7.18 REST API (Java 8) with Supabase Auth (JWT) and PostgreSQL (Supabase).

### Layered structure (`org.example.waspapi`)

- **`controller/`** — REST endpoints. All endpoints require JWT auth except Swagger UI paths.
- **`service/`** — Business logic. Services extract the user email from the JWT token via the standard `email` claim (see `Constants.SUPABASE_EMAIL_CLAIM`).
- **`repository/`** — Spring Data JPA interfaces extending `JpaRepository`.
- **`model/`** — JPA entities (`User`, `Game`, `Theme`, `Subscription`). `User` uses `email` as its primary key.
- **`dto/requests/`** and **`dto/responses/`** — Request/response DTOs, organized by domain (users, game, subscription).
- **`config/`** — `SecurityConfig` (OAuth2 resource server with JWT) and `OpenApiConfig` (bearerAuth scheme).
- **`exceptions/`** — `HandledException` wraps errors with HTTP status codes.
- **`Constants.java`** — Shared constants (Supabase email claim key, error messages).

### Key domain relationships

- **Subscription** links a User to a Game with a role (OWNER, ADMIN, PLAYER, etc.) and serves as the access control mechanism.
- **Game** belongs to a **Theme** (ManyToOne). Games use soft deletes (`isDeleted` flag).
- Creating a game auto-subscribes the creator as OWNER. Game mutations require admin-level subscription.

### Code quality

- **Spotless** auto-formats with Google Java Format on every build (`validate` phase).
- **Checkstyle** enforces Google style guide on `verify` phase — builds fail on violations.
- **SonarCloud** runs via GitHub Actions on push/PR to `master`.

### Configuration

- `application.yml` — Supabase Auth JWT (jwk-set-uri), server port.
- `application.properties` — PostgreSQL datasource (Supabase), JPA/Hibernate settings. DB credentials come from env vars `DB_USER` and `DB_PASS` (defined in `.env`).
