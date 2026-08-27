package simulations.chains;

import io.gatling.javaapi.core.ChainBuilder;
import simulations.config.Config;
import simulations.config.Feeders;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public final class PaymentChains {

    private PaymentChains() {}

    /**
     * Creates a payment for whatever "orderId"/"customerEmail" are already in the session,
     * then walks it through the same PENDING -> COMPLETED transition the frontend's
     * "Mark Completed" button drives (see Home.vue's setPaymentStatus and
     * PaymentServiceImpl.updateStatus - there is no PUT here, only create-once-then-transition,
     * so this chain deliberately never attempts to edit the amount/method after creation).
     */
    public static ChainBuilder createAndCompletePayment() {
        return feed(Feeders.paymentMethod())
                .exec(
                        http("Payment - Create")
                                .post(Config.PAYMENT_BASE_URL + "/api/payments")
                                .header("Authorization", "Bearer #{authToken}")
                                .header("Content-Type", "application/json")
                                .body(StringBody(
                                        "{\"orderId\":#{orderId},\"customerEmail\":\"#{customerEmail}\"," +
                                        "\"amount\":49.99,\"method\":\"#{method}\"}"))
                                .check(status().is(201))
                                .check(jsonPath("$.paymentId").saveAs("paymentId"))
                                .check(jsonPath("$.status").is("PENDING"))
                )
                .exec(
                        http("Payment - Mark Completed")
                                .patch(Config.PAYMENT_BASE_URL + "/api/payments/#{paymentId}/status")
                                .header("Authorization", "Bearer #{authToken}")
                                .header("Content-Type", "application/json")
                                .body(StringBody("{\"status\":\"COMPLETED\"}"))
                                .check(status().is(200))
                                .check(jsonPath("$.status").is("COMPLETED"))
                );
    }
}
