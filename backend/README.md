# Backend Setup

Two independently deployable Spring Boot services, both backed by the same Northwind
SQL Server instance for now (Phase 1 of an SOA migration - see below).

| Service           | Port | Owns                    | Module              |
|--------------------|------|--------------------------|----------------------|
| `auth-service`     | 8082 | Accounts, Customers      | `auth-service/`      |
| `ecommerce-service`| 8083 | Categories, Products, Orders, OrderDetails, Employees | `ecommerce-service/` |

`ecommerce-service` never issues JWTs, only verifies them - it needs `auth-service`'s
`app.jwt.secret` to match exactly, since a request there is authenticated by checking
the token's signature locally rather than calling `auth-service` back.

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

Both services need to be running for the frontend to fully work (registration/login via
`auth-service`, everything else via `ecommerce-service`).

```bash
cd auth-service && mvn spring-boot:run
```
```bash
cd ecommerce-service && mvn spring-boot:run
```

Or build/test everything from the reactor root:
```bash
mvn install        # builds both modules
mvn test            # unit tests only, no DB required
mvn verify           # also runs *IT.java against NorthWind_clone
```

## SOA migration status

This split is Phase 1 of decomposing the original single-module monolith into
separate services: independently deployable modules, but still sharing one database
and duplicating the small amount of code (`JwtService`, `Account` entity/repository)
each service needs to verify a caller's identity on its own. See git history on this
branch for the previous single-module layout.
