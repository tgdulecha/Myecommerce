# Load Tests (Gatling)

Black-box HTTP load tests against `auth-service` (8082), `ecommerce-service` (8083) and
`payment-service` (8084) together - not unit/integration tests, and not part of `mvn
install`/`test`/`verify` at the `backend/` root. This module is intentionally standalone
(no `<parent>`, not in `backend/pom.xml`'s `<modules>`) - see the comment at the top of
`pom.xml` for why.

## Prerequisites

1. All three services running locally on their default ports, against a real database with
   seeded Northwind data (`Config.KNOWN_ORDER_ID` defaults to `10248`, which must exist).
2. A shared test account for the scenarios that don't register their own
   (`CatalogBrowsingLoadSimulation`, `PaymentServiceStressSimulation`) - create it once:

   ```bash
   curl -s -X POST http://localhost:8082/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"email":"gatling-shared-user@example.com","password":"Passw0rd!23","companyName":"Gatling Shared Co","contactName":"Gatling Bot","address":"1 Load Test Ave","city":"Testville","country":"Testland"}'
   ```

   Override the email/password `Simulation` uses via `-DsharedUserEmail=... -DsharedUserPassword=...`
   if you'd rather point at an existing account.

## Simulations

| Simulation | What it measures | Users |
|---|---|---|
| `SmokeTestSimulation` | The full chain still works end to end (register → catalog → orders → pay) | 3, `atOnceUsers` |
| `CatalogBrowsingLoadSimulation` | ecommerce-service read capacity (Category/Products) | ramp + sustained |
| `CheckoutJourneySimulation` | The realistic new-customer journey across all 3 services | ramp + sustained + spike |
| `PaymentServiceStressSimulation` | payment-service alone, isolated from the other two | ramp-per-sec + sustained peak |

## Running

```bash
cd load-tests
mvn gatling:test -Dgatling.simulationClass=simulations.SmokeTestSimulation
```

Any simulation can be pointed at a different environment or given a different injection
shape without touching Java - see `Config.java` for the full list of `-D` overrides, e.g.:

```bash
mvn gatling:test -Dgatling.simulationClass=simulations.CheckoutJourneySimulation \
  -DecommerceBaseUrl=https://staging.example.com \
  -DrampUsers=200 -DrampDurationSec=120 -DsustainUsersPerSec=30 -DsustainDurationSec=300
```

HTML reports land in `target/gatling/<simulation-name>-<timestamp>/index.html` - open that
in a browser for response-time percentiles, requests/sec over time, and per-request
breakdowns.

## Layout

```
src/test/java/simulations/
  config/
    Config.java    - base URLs + injection profile knobs, all -D overridable
    Feeders.java    - registration data (randomized company prefix - see its Javadoc for why)
                       and payment method
  chains/
    AuthChains.java     - register+login / login-as-shared-user, both leave "authToken" in session
    CatalogChains.java  - browse Categories/Products
    OrderChains.java    - page through Orders, pick a real orderId into the session
    PaymentChains.java  - create a payment, then PATCH it to COMPLETED
  *Simulation.java  - each wires chains together with its own injection profile + assertions
```

## A caveat worth knowing before you scale these up

`AuthChains.registerAndLogin()` creates a **real** `Account`/`Customer` row per virtual user
and never cleans it up - fine for `SmokeTestSimulation` (3 users) and even a few hundred for
`CheckoutJourneySimulation`, but a sustained high-volume run will leave thousands of rows
behind. `PaymentChains.createAndCompletePayment()` does the same to `Payments`. Point these
at a disposable/test database, not the real `NorthWind` instance, if you're going to run them
at real scale repeatedly.
