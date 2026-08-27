package simulations.config;

/**
 * Every knob here is overridable via -D system properties so the same simulations run
 * against a laptop (defaults below) or a staging environment without editing Java:
 *   mvn gatling:test -Dgatling.simulationClass=simulations.CheckoutJourneySimulation \
 *       -DecommerceBaseUrl=https://staging.example.com -DrampUsers=200
 */
public final class Config {

    private Config() {}

    public static final String AUTH_BASE_URL = System.getProperty("authBaseUrl", "http://localhost:8082");
    public static final String ECOMMERCE_BASE_URL = System.getProperty("ecommerceBaseUrl", "http://localhost:8083");
    public static final String PAYMENT_BASE_URL = System.getProperty("paymentBaseUrl", "http://localhost:8084");

    // Injection profile knobs shared across simulations - see each Simulation class for
    // how they're combined into its specific ramp/sustain/spike shape.
    public static final int RAMP_USERS = Integer.getInteger("rampUsers", 20);
    public static final int RAMP_DURATION_SEC = Integer.getInteger("rampDurationSec", 30);
    public static final int SUSTAIN_USERS_PER_SEC = Integer.getInteger("sustainUsersPerSec", 5);
    public static final int SUSTAIN_DURATION_SEC = Integer.getInteger("sustainDurationSec", 60);

    // A pre-registered account for read-only / targeted scenarios that don't need a unique
    // customer identity and shouldn't hammer auth-service's /api/auth/register (see README
    // "Prerequisites" for the one-time curl command that creates it).
    public static final String SHARED_USER_EMAIL = System.getProperty("sharedUserEmail", "gatling-shared-user@example.com");
    public static final String SHARED_USER_PASSWORD = System.getProperty("sharedUserPassword", "Passw0rd!23");

    // A known-good, always-present order from the seeded Northwind data (see
    // ecommerce-service's northwind_clone_schema.sql / the real NorthWind DB) - used by
    // scenarios that want to isolate payment-service without depending on ecommerce-service's
    // order list under load too.
    public static final String KNOWN_ORDER_ID = System.getProperty("knownOrderId", "10248");
}
