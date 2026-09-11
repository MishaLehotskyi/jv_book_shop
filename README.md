# Book Shop API

A REST API for an online book shop, built with Spring Boot. It covers the full customer journey —
registration and JWT login, browsing and searching books by category, filling a shopping cart, and
placing orders — plus an admin surface for managing the catalogue and order statuses.

## Tech stack

| Area | Technology |
| --- | --- |
| Language / runtime | Java 17 |
| Framework | Spring Boot 4.0.5 (Web, Data JPA, Security, Validation) |
| Database | MySQL 8 (H2 in-memory for tests) |
| Migrations | Liquibase |
| Mapping | MapStruct |
| Auth | Stateless JWT (jjwt) |
| Docs | springdoc-openapi (Swagger UI) |
| Build / lint | Maven, Checkstyle (Google style) |
| Infrastructure | Docker, Docker Compose |

## Architecture

The layering is strict and uniform: `controller → service (interface + impl) → repository`. Entities
never leave the service layer — every request and response body is a Java `record` in `dto/`, and
MapStruct mappers convert between the two.

- **Authentication** is stateless JWT. `JwtAuthenticationFilter` runs ahead of Spring Security's
  `UsernamePasswordAuthenticationFilter`, validates the `Bearer` token and loads the user through
  `CustomUserDetailsService`. Only `/auth/login`, `/auth/registration` and the Swagger paths are public.
- **Authorization** is per-endpoint via `@PreAuthorize` rather than URL patterns — `USER` reads the
  catalogue and owns a cart and orders, `ADMIN` writes the catalogue and updates order statuses.
- **Soft delete**: `Book`, `Category` and `User` use Hibernate `@SQLDelete` + `@SQLRestriction`, so a
  delete becomes an `UPDATE is_deleted = true` and every query is filtered transparently.
- **Book search** uses a pluggable Specification pattern (`repository/spec/`) — a new filterable field
  is a new `SpecificationProvider<Book>` component, auto-collected by the manager.
- **Errors** are normalised by `CustomGlobalExceptionHandler` into a `{timestamp, errors: [...]}` body.

## API overview

All routes are served under the `/api` context path.

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/auth/registration` | public | Register a new user |
| `POST` | `/auth/login` | public | Exchange credentials for a JWT |
| `GET` | `/books` | `USER` | List books (paginated, sortable) |
| `GET` | `/books/search` | `USER` | Search books by title, author, price, … |
| `GET` | `/books/{id}` | `USER` | Get a single book |
| `POST` | `/books` | `ADMIN` | Create a book |
| `PUT` | `/books/{id}` | `ADMIN` | Update a book |
| `DELETE` | `/books/{id}` | `ADMIN` | Soft-delete a book |
| `GET` | `/categories` | `USER` | List categories |
| `GET` | `/categories/{id}` | `USER` | Get a single category |
| `GET` | `/categories/{id}/books` | `USER` | List books in a category |
| `POST` | `/categories` | `ADMIN` | Create a category |
| `PUT` | `/categories/{id}` | `ADMIN` | Update a category |
| `DELETE` | `/categories/{id}` | `ADMIN` | Soft-delete a category |
| `GET` | `/cart` | `USER` | Get the current user's cart |
| `POST` | `/cart` | `USER` | Add a book to the cart |
| `PUT` | `/cart/items/{cartItemId}` | `USER` | Change an item's quantity |
| `DELETE` | `/cart/items/{cartItemId}` | `USER` | Remove an item from the cart |
| `POST` | `/orders` | `USER` | Place an order from the cart |
| `GET` | `/orders` | `USER` | List the current user's orders |
| `GET` | `/orders/{orderId}/items` | `USER` | List items of an order |
| `GET` | `/orders/{orderId}/items/{itemId}` | `USER` | Get one item of an order |
| `PATCH` | `/orders/{id}` | `ADMIN` | Update an order's status |

Interactive documentation is available at
[`http://localhost:8080/api/swagger-ui/index.html`](http://localhost:8080/api/swagger-ui/index.html)
once the application is running (use the port you mapped — see below).

## Running with Docker

### Prerequisites

- Docker and Docker Compose (Docker Desktop already bundles both)

### 1. Create your `.env`

The repository ships an `.env.template` listing every variable the stack needs, with no values — real
credentials must never be committed, so `.env` itself is git-ignored. Copy the template and fill it in:

```bash
cp .env.template .env
```

| Variable | Description | Example |
| --- | --- | --- |
| `MYSQLDB_DATABASE` | Name of the schema created on first start | `book_shop` |
| `MYSQLDB_USER` | User the application connects with | `root` |
| `MYSQLDB_ROOT_PASSWORD` | Password for the MySQL `root` user — pick your own | `root_password` |
| `MYSQLDB_LOCAL_PORT` | Host port mapped to MySQL, handy if `3306` is already taken | `3307` |
| `MYSQLDB_DOCKER_PORT` | Port MySQL listens on inside the network | `3306` |
| `SPRING_LOCAL_PORT` | Host port you will call the API on | `8081` |
| `SPRING_DOCKER_PORT` | Port the application listens on inside the container | `8080` |
| `DEBUG_PORT` | Remote debug port exposed by the container | `5005` |
| `JWT_SECRET` | Secret used to sign tokens — use a long random string, at least 32 characters | `<your-own-long-random-secret>` |
| `JWT_EXPIRATION` | Token lifetime in milliseconds | `3600000` |

`MYSQLDB_USER` is the account the API authenticates with and `MYSQLDB_ROOT_PASSWORD` is its password;
by default that is MySQL's built-in `root` account, so the two are used together.

### 2. Build and start

```bash
docker compose up --build
```

Compose builds the application image, starts MySQL, waits for its health check to pass, then starts the
API and applies the Liquibase migrations. The API is then available at
`http://localhost:${SPRING_LOCAL_PORT}/api` — with the example values above, that is
`http://localhost:8081/api`, and Swagger UI at `http://localhost:8081/api/swagger-ui/index.html`.

To stop the stack, and optionally drop the database volume with it:

```bash
docker compose down      # stop, keep data
docker compose down -v   # stop and delete the MySQL volume
```

The container exposes a JDWP debug agent on `DEBUG_PORT`, so you can attach a remote debugger from
your IDE to `localhost:5005`.

## Running locally without Docker

You will need JDK 17 and a MySQL 8 instance on `localhost:3306` with a `book_shop` schema. The Maven
wrapper is committed, so no local Maven installation is required.

```bash
./mvnw spring-boot:run       # bash
mvnw.cmd spring-boot:run     # PowerShell
```

The datasource falls back to `localhost:3306/book_shop` with user `root` when the `MYSQLDB_*` variables
are not set, so a plain local run needs no extra configuration. Override any of them as environment
variables if your setup differs.

## Tests and linting

```bash
./mvnw verify                # what CI runs: compile + checkstyle + tests
./mvnw checkstyle:check      # lint only (also bound to the compile phase)
./mvnw test                  # tests only
```

Tests run against an in-memory H2 database and never touch MySQL. Checkstyle runs with
`severity=error` during `compile`, so a style violation fails the build before the tests run.

## Database changes

The schema is Liquibase-only (`spring.jpa.hibernate.ddl-auto=validate`), so an entity change without a
matching changelog fails startup. Add a new numbered YAML file under
`src/main/resources/db/changelog/changes/` and append an `include` to `db.changelog-master.yaml`.
Never edit a changeset that has already been applied.
