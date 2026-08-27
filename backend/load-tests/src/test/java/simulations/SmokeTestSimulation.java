package simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import simulations.chains.AuthChains;
import simulations.chains.CatalogChains;
import simulations.chains.OrderChains;
import simulations.chains.PaymentChains;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Fast, low-volume sanity check that the full cross-service chain still works end to end:
 * auth-service issues a token, ecommerce-service accepts it, payment-service accepts it too.
 * Meant to run in a few seconds as a CI/pre-deploy gate - it proves the chain is wired
 * correctly, not that it can bear load (see CheckoutJourneySimulation for that).
 *
 * Run: mvn gatling:test -Dgatling.simulationClass=simulations.SmokeTestSimulation
 */
public class SmokeTestSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .acceptHeader("application/json")
            .userAgentHeader("GatlingSmokeTest");

    ScenarioBuilder journey = scenario("Smoke - full checkout chain")
            .exec(AuthChains.registerAndLogin())
            .exec(CatalogChains.browseCatalog())
            .exec(OrderChains.browseOrdersAndPickOne())
            .exec(PaymentChains.createAndCompletePayment());

    {
        setUp(
                journey.injectOpen(atOnceUsers(3))
        )
        .protocols(httpProtocol)
        .assertions(
                global().failedRequests().count().is(0L)
        );
    }
}
