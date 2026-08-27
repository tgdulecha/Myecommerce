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
