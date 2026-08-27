package simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import simulations.chains.AuthChains;
import simulations.chains.CatalogChains;
import simulations.config.Config;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Read-heavy load against ecommerce-service's catalog endpoints (Categories/Products) - the
 * highest-traffic path in a real storefront. Every virtual user logs in as the same
 * pre-seeded shared account (see README "Prerequisites") instead of registering, so this
 * measures ecommerce-service's read capacity rather than auth-service's write throughput.
 *
 * Run: mvn gatling:test -Dgatling.simulationClass=simulations.CatalogBrowsingLoadSimulation
 * Tune: -DrampUsers=100 -DrampDurationSec=60 -DsustainUsersPerSec=20 -DsustainDurationSec=120
 */
public class CatalogBrowsingLoadSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.acceptHeader("application/json");

    ScenarioBuilder journey = scenario("Browse catalog")
            .exec(AuthChains.loginAsSharedUser())
            .repeat(3).on(
                    CatalogChains.browseCatalog()
                            .pause(Duration.ofMillis(300), Duration.ofSeconds(1))
            );

    {
        setUp(
                journey.injectOpen(
                        rampUsers(Config.RAMP_USERS).during(Duration.ofSeconds(Config.RAMP_DURATION_SEC)),
                        constantUsersPerSec(Config.SUSTAIN_USERS_PER_SEC)
                                .during(Duration.ofSeconds(Config.SUSTAIN_DURATION_SEC))
                )
        )
        .protocols(httpProtocol)
        .assertions(
                global().responseTime().percentile(95).lt(1000),
                global().successfulRequests().percent().gt(99.0)
        );
    }
}
