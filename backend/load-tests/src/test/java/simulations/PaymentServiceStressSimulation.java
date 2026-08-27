package simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import simulations.chains.AuthChains;
import simulations.chains.PaymentChains;
import simulations.config.Config;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Isolates payment-service under stress, independent of ecommerce-service/auth-service
 * capacity. Every user logs in once (shared account) then hammers create + status-transition
 * against a fixed, known-good orderId (see Config.KNOWN_ORDER_ID) instead of paging through
 * ecommerce-service's orders first, so the read side of ecommerce-service isn't part of what's
 * being measured here. payment-service is the newest module and, unlike ecommerce-service,
 * hasn't had its Tomcat thread pool tuned (compare application.properties in both services) -
 * this is the test that would show whether it needs the same treatment.
 *
 * Run: mvn gatling:test -Dgatling.simulationClass=simulations.PaymentServiceStressSimulation
 * Tune: -DsustainUsersPerSec=40 -DsustainDurationSec=120
 */
public class PaymentServiceStressSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.acceptHeader("application/json");

    ScenarioBuilder journey = scenario("Payment create+complete stress")
            .exec(AuthChains.loginAsSharedUser())
            .exec(session -> session.set("orderId", Config.KNOWN_ORDER_ID))
            .exec(PaymentChains.createAndCompletePayment());

    {
        int peakUsersPerSec = Config.SUSTAIN_USERS_PER_SEC * 4;

        setUp(
                journey.injectOpen(
                        rampUsersPerSec(1).to(peakUsersPerSec).during(Duration.ofSeconds(Config.RAMP_DURATION_SEC)),
                        constantUsersPerSec(peakUsersPerSec).during(Duration.ofSeconds(Config.SUSTAIN_DURATION_SEC))
                )
        )
        .protocols(httpProtocol)
        .assertions(
                global().responseTime().percentile(99).lt(3000),
                global().failedRequests().percent().lt(2.0)
        );
    }
}
