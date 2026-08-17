# Backend Setup

Spring Boot app backed by a Northwind SQL Server instance (port 1433). Runs on port 8083.

## Required environment variables

`spring.datasource.username` and `spring.datasource.password` are read from the environment — the app will fail to start without them.

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
Run/Debug Configurations → `EcommerceApplication` → Environment variables → add `DB_USERNAME=corso7;DB_PASSWORD=corso7`.

## Run
```bash
mvn spring-boot:run
```
