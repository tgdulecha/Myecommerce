package simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import simulations.chains.AuthChains;
import simulations.chains.CatalogChains;
import simulations.chains.OrderChains;
import simulations.chains.PaymentChains;
import simulations.config.Config;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * The realistic end-to-end journey: a brand-new customer registers, browses the catalog,
 * finds an order, and pays for it - exercising all three services (auth -> ecommerce ->
 * payment) and the JWT hand-off between them in a single virtual user. Staged injection
 * (ramp -> sustained -> spike) mirrors a marketing-push traffic shape rather than a flat load,
 * and is the test that would justify (or rule out) tuning server.tomcat.threads.max on
 * auth-service/payment-service the way ecommerce-service's application.properties already does.
 *
 * Run: mvn gatling:test -Dgatling.simulationClass=simulations.CheckoutJourneySimulation
 * Tune: -DrampUsers=50 -DrampDurationSec=60 -DsustainUsersPerSec=10 -DsustainDurationSec=120
 */
public class CheckoutJourneySimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.acceptHeader("application/json");

    ScenarioBuilder journey = scenario("New customer checkout")
            .exec(AuthChains.registerAndLogin())
            .pause(Duration.ofMillis(200), Duration.ofSeconds(1))
            .exec(CatalogChains.browseCatalog())
            .pause(Duration.ofMillis(200), Duration.ofSeconds(1))
            .exec(OrderChains.browseOrdersAndPickOne())
            .pause(Duration.ofMillis(200), Duration.ofSeconds(1))
            .exec(PaymentChains.createAndCompletePayment());

    {
        setUp(
                journey.injectOpen(
                        rampUsers(Config.RAMP_USERS).during(Duration.ofSeconds(Config.RAMP_DURATION_SEC)),
                        constantUsersPerSec(Config.SUSTAIN_USERS_PER_SEC)
                                .during(Duration.ofSeconds(Config.SUSTAIN_DURATION_SEC)),
                        atOnceUsers(Config.RAMP_USERS * 2) // spike, e.g. a promo email landing all at once
                )
        )
        .protocols(httpProtocol)
        .assertions(
                global().responseTime().percentile(95).lt(2000),
                global().failedRequests().percent().lt(1.0)
        );
    }
}
