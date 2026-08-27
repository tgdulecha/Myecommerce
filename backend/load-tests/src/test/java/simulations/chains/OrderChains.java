package simulations.chains;

import io.gatling.javaapi.core.ChainBuilder;
import simulations.config.Config;

import java.util.List;
import java.util.Random;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public final class OrderChains {

    private OrderChains() {}

    /**
     * Pulls one page of orders and remembers a real orderId from it. payment-service never
     * joins into ecommerce-service's Orders table (see Payment entity's Javadoc - it only
     * stores the orderId/customerEmail scalars it's given), so a load test that pays against
     * a fabricated orderId would exercise none of the FK-adjacent realism a real checkout has -
     * this keeps "orderId" tied to something that actually exists.
     */
    public static ChainBuilder browseOrdersAndPickOne() {
        return exec(
                http("Ecommerce - List Orders (page 1)")
                        .get(Config.ECOMMERCE_BASE_URL + "/api/orders?page=1&size=20")
                        .header("Authorization", "Bearer #{authToken}")
                        .check(status().is(200))
                        .check(jsonPath("$.content[*].orderId").findAll().saveAs("orderIds"))
        ).exec(session -> {
            List<Object> ids = session.getList("orderIds");
            Object pick = ids.get(new Random().nextInt(ids.size()));
            return session.set("orderId", pick);
        });
    }
}
