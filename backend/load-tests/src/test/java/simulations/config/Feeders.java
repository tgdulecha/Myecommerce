package simulations.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Feeders {

    private Feeders() {}

    private static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new Random();
    private static final String[] PAYMENT_METHODS = {"CreditCard", "PayPal", "BankTransfer"};

    /**
     * One row per brand-new registrant. The company-name prefix is randomized per row -
     * AccountServiceImpl.generateCustomerId derives the CustomerID from the first 5 letters
     * of companyName and only retries 9 times on a collision before throwing (see
     * auth-service/src/main/java/org/course/authservice/service/AccountServiceImpl.java).
     * A fixed company name would start failing registrations after ~10 virtual users for a
     * reason that has nothing to do with real load capacity - randomizing the prefix here
     * keeps that collision essentially impossible at any realistic test volume.
     */
    public static Iterator<Map<String, Object>> newRegistrant() {
        return Stream.generate((Supplier<Map<String, Object>>) () -> {
            String prefix = randomAlpha(5);
            String uid = UUID.randomUUID().toString().substring(0, 8);

            Map<String, Object> row = new HashMap<>();
            row.put("email", "gatling-" + uid + "@example.com");
            row.put("password", "Passw0rd!23");
            row.put("companyName", prefix + " Load Testing Co");
            row.put("contactName", "Gatling Bot");
            row.put("address", "1 Load Test Ave");
            row.put("city", "Testville");
            row.put("country", "Testland");
            return row;
        }).iterator();
    }

    public static Iterator<Map<String, Object>> paymentMethod() {
        return Stream.generate((Supplier<Map<String, Object>>) () -> {
            Map<String, Object> row = new HashMap<>();
            row.put("method", PAYMENT_METHODS[RANDOM.nextInt(PAYMENT_METHODS.length)]);
            return row;
        }).iterator();
    }

    private static String randomAlpha(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(ALPHA.charAt(RANDOM.nextInt(ALPHA.length())));
        return sb.toString();
    }
}
