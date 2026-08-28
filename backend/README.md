# Backend Setup

Three independently deployable Spring Boot services, all backed by the same Northwind
SQL Server instance for now (Phase 1 of an SOA migration - see below).

| Service           | Port | Owns                    | Module              |
|--------------------|------|--------------------------|----------------------|
| `auth-service`     | 8082 | Accounts, Customers      | `auth-service/`      |
| `ecommerce-service`| 8083 | Categories, Products, Orders, OrderDetails, Employees | `ecommerce-service/` |
| `payment-service`  | 8084 | Payments                | `payment-service/`   |

`ecommerce-service` and `payment-service` never issue JWTs, only verify them - each
needs `auth-service`'s `app.jwt.secret` to match exactly, since a request there is
authenticated by checking the token's signature locally rather than calling
`auth-service` back.

`payment-service` owns the `Payments` table outright - it's new, not inherited from
the Northwind monolith, so unlike the `Accounts` copy every service still carries for
JWT auth, nothing else reads or writes it. Run
`payment-service/src/main/resources/db/migration/V1__create_payments_table.sql`
against `NorthWind` once before starting it for the first time (`ddl-auto=none`
everywhere - Hibernate never creates or alters tables here).

## Required environment variables

`spring.datasource.username` and `spring.datasource.password` are read from the environment - each service will fail to start without them.

| Variable      | Value (local dev) |
|---------------|--------------------|
| `DB_USERNAME` | `corso7`           |
| `DB_PASSWORD` | `corso7`           |

### PowerShell
```powershell
$env:DB_USERNAME = "corso7"
$env:DB_PASSWORD = "corso7"
```

### bash / git bash
```bash
export DB_USERNAME=corso7
export DB_PASSWORD=corso7
```

### IntelliJ IDEA
Run/Debug Configurations → `AuthServiceApplication` / `EcommerceApplication` → Environment variables → add `DB_USERNAME=corso7;DB_PASSWORD=corso7`.

## Run

All three services need to be running for the frontend to fully work
(registration/login via `auth-service`, catalog/orders via `ecommerce-service`,
checkout payments via `payment-service`).

```bash
cd auth-service && mvn spring-boot:run
```
```bash
cd ecommerce-service && mvn spring-boot:run
```
```bash
cd payment-service && mvn spring-boot:run
```

Or build/test everything from the reactor root:
```bash
mvn install        # builds all modules
mvn test            # unit tests only, no DB required
mvn verify           # also runs *IT.java against NorthWind_clone
```

## Docker

Each service ships as its own image, built from its own `Dockerfile`, wired together
with a single `docker-compose.yml` at the reactor root.

| Service             | Dockerfile                     | Host port |
|---------------------|---------------------------------|-----------|
| `auth-service`       | `auth-service/Dockerfile`       | 8082      |
| `ecommerce-service`  | `ecommerce-service/Dockerfile`  | 8083      |
| `payment-service`    | `payment-service/Dockerfile`    | 8084      |

Docker Desktop must be running first - open it from the Start menu (or run
`Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"` in PowerShell) and
wait for it to report "Engine running" before using any `docker compose` command
below.

SQL Server itself is **not** containerized - all three services connect out to
whatever `NorthWind` instance is already running on your machine, same as running
them bare-metal. Run
`payment-service/src/main/resources/db/migration/V1__create_payments_table.sql`
against it once before the first `docker compose up`, same as bare-metal.

### 1. Set environment variables

`docker compose` automatically loads a `.env` file placed next to `docker-compose.yml`,
so instead of exporting the variables from [Required environment variables](#required-environment-variables)
into your shell every time:

```bash
cp .env.example .env
```

The defaults in `.env.example` already match the dev credentials above - edit `.env`
if yours differ. `.env` is gitignored; never commit real credentials into it.

### 2. Build and run

```bash
docker compose up --build
```

Builds all three images and starts the containers in one step, each publishing the
same port it uses bare-metal (8082/8083/8084). `--build` forces a rebuild first, so
this is the command to reach for after pulling changes or editing a Dockerfile - drop
it for a plain restart on already-built images (`docker compose up`).

Other useful variants:

```bash
docker compose up --build -d        # detached (background)
docker compose up --build auth-service   # just one service
docker compose build auth-service        # build only, don't start
docker compose down                      # stop and remove containers
```

### How a single Dockerfile builds one module out of the Maven reactor

Each service's `Dockerfile` uses the **repo root as its build context** (so it can see
the parent `pom.xml`), builds in two stages, and only ever compiles its own module:

1. **Build stage** (`maven:3.9-eclipse-temurin-21`) - copies the parent `pom.xml` plus
   every module's `pom.xml` first (Maven needs the full reactor's module list to
   resolve the parent, even though only one module gets built), runs
   `dependency:go-offline` so dependency downloads are cached in their own Docker
   layer, then copies in *only that module's* `src/` and runs
   `mvn -pl <module> package -DskipTests`. Editing `payment-service` source, for
   example, never invalidates `auth-service`'s cached layers.
2. **Runtime stage** (`eclipse-temurin:21-jre`) - copies just the built jar out of the
   build stage. No Maven, no source, no build tooling in the final image.

### Why `SPRING_DATASOURCE_URL` is overridden in `docker-compose.yml`

`application.properties` in every service hardcodes
`jdbc:sqlserver://localhost:1433;...`. That's fine bare-metal or from an IDE, but
inside a container `localhost` means the container itself, not your machine - so
`docker-compose.yml` sets the `SPRING_DATASOURCE_URL` environment variable per
service, pointing at `host.docker.internal` instead. Spring Boot's relaxed env-var
binding applies that over the property in `application.properties` automatically, no
code change needed. `extra_hosts: host.docker.internal:host-gateway` makes
`host.docker.internal` resolve on Linux Docker hosts too, not just Docker Desktop
(where it works out of the box).

## SOA migration status

This split is Phase 1 of decomposing the original single-module monolith into
separate services: independently deployable modules, but still sharing one database
and duplicating the small amount of code (`JwtService`, `Account` entity/repository)
each service needs to verify a caller's identity on its own. See git history on this
branch for the previous single-module layout.

`payment-service` is the first module added directly as a separate service rather
than split out of the monolith, and the first to own a table with no other service
touching it - a preview of what Phase 2 (one database per service) should look like
for the rest of the schema too.
