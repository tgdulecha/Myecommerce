package simulations.chains;

import io.gatling.javaapi.core.ChainBuilder;
import simulations.config.Config;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/** Read-only browsing against ecommerce-service's catalog - every route needs a bearer
 *  token (SecurityConfig.anyRequest().authenticated()), so "authToken" must already be in
 *  the session (see AuthChains). */
public final class CatalogChains {

    private CatalogChains() {}

    public static ChainBuilder browseCatalog() {
        return exec(
                http("Ecommerce - List Categories")
                        .get(Config.ECOMMERCE_BASE_URL + "/api/category")
                        .header("Authorization", "Bearer #{authToken}")
                        .check(status().is(200))
        )
        .pause(1)
        .exec(
                http("Ecommerce - List Products")
                        .get(Config.ECOMMERCE_BASE_URL + "/api/products")
                        .header("Authorization", "Bearer #{authToken}")
                        .check(status().is(200))
        );
    }
}
