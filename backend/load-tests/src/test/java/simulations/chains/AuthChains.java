package simulations.chains;

import io.gatling.javaapi.core.ChainBuilder;
import simulations.config.Config;
import simulations.config.Feeders;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public final class AuthChains {

    private AuthChains() {}

    /**
     * Registers a brand-new customer account, logs in, and leaves "authToken" +
     * "customerEmail" in the session for every later request in the chain to reuse -
     * mirrors what SignUp.vue -> register() -> login() does in the frontend
     * (see frontend/src/js/auth.js's register()).
     */
    public static ChainBuilder registerAndLogin() {
        return feed(Feeders.newRegistrant())
                .exec(
                        http("Auth - Register")
                                .post(Config.AUTH_BASE_URL + "/api/auth/register")
                                .header("Content-Type", "application/json")
                                .body(StringBody(
                                        "{\"email\":\"#{email}\",\"password\":\"#{password}\"," +
                                        "\"companyName\":\"#{companyName}\",\"contactName\":\"#{contactName}\"," +
                                        "\"address\":\"#{address}\",\"city\":\"#{city}\",\"country\":\"#{country}\"}"))
                                .check(status().is(201))
                )
                .exec(
                        http("Auth - Login")
                                .post(Config.AUTH_BASE_URL + "/api/auth/login")
                                .header("Content-Type", "application/json")
                                .body(StringBody("{\"email\":\"#{email}\",\"password\":\"#{password}\"}"))
                                .check(status().is(200))
                                .check(jsonPath("$.token").saveAs("authToken"))
                )
                .exec(session -> session.set("customerEmail", session.getString("email")));
    }

    /**
     * Logs in with a single pre-seeded shared account instead of registering - for scenarios
     * that don't need a unique customer identity and shouldn't add write load to
     * /api/auth/register just to get a token (see README "Prerequisites").
     */
    public static ChainBuilder loginAsSharedUser() {
        return exec(
                http("Auth - Login (shared user)")
                        .post(Config.AUTH_BASE_URL + "/api/auth/login")
                        .header("Content-Type", "application/json")
                        .body(StringBody(
                                "{\"email\":\"" + Config.SHARED_USER_EMAIL + "\"," +
                                "\"password\":\"" + Config.SHARED_USER_PASSWORD + "\"}"))
                        .check(status().is(200))
                        .check(jsonPath("$.token").saveAs("authToken"))
        ).exec(session -> session.set("customerEmail", Config.SHARED_USER_EMAIL));
    }
}
