# API tests (Postman / Newman)

Runs the same requests you'd otherwise click through in Postman, as an automatable
collection. Unlike `AuthControllerIT`, these hit a **real running server** over
HTTP and make **real, non-rolled-back writes** - each run inserts a new
`Customers`/`Accounts` row (unique email/company name per run via a timestamp),
so only ever point this at `NorthWind_clone`, never the real `NorthWind` database.

## Prerequisites

1. The schema from `../auth-service/src/test/resources/sql/northwind_clone_schema.sql`
   exists in `NorthWind_clone` (same schema `ecommerce-service`'s copy uses - it's one
   shared database for now).
2. `auth-service` is running **against that database**, not the real one - this
   collection only exercises `/api/auth`, which now lives there (port 8082):

   ```bash
   cd ../auth-service
   SPRING_PROFILES_ACTIVE=test DB_USERNAME=... DB_PASSWORD=... mvn spring-boot:run
   ```

   (PowerShell: `$env:SPRING_PROFILES_ACTIVE="test"; mvn spring-boot:run`)

## Run

```bash
npm install
npm run test:api
```

Each run leaves behind one extra row in `Customers`/`Accounts` (the "Register
(success)" account). Periodically truncate `NorthWind_clone`'s `Accounts` and
`Customers` tables if that accumulation bothers you - it's a disposable test DB.
