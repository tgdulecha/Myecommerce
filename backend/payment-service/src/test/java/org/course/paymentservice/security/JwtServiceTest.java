package org.course.paymentservice.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-key-needs-to-be-at-least-32-bytes";

    @Test
    void generateTokenRoundTripsTheEmail() {
        JwtService jwtService = new JwtService(SECRET, 60_000);

        String token = jwtService.generateToken("jane@example.com", "Customer");

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractEmail(token)).isEqualTo("jane@example.com");
    }

    @Test
    void isValidRejectsGarbageInput() {
        JwtService jwtService = new JwtService(SECRET, 60_000);

        assertThat(jwtService.isValid("not-a-real-token")).isFalse();
    }

    @Test
    void isValidRejectsExpiredToken() throws InterruptedException {
        JwtService jwtService = new JwtService(SECRET, 1);

        String token = jwtService.generateToken("jane@example.com", "Customer");
        Thread.sleep(50);

        assertThat(jwtService.isValid(token)).isFalse();
    }

    @Test
    void isValidRejectsTokenSignedWithADifferentSecret() {
        JwtService issuer = new JwtService(SECRET, 60_000);
        JwtService verifier = new JwtService("a-completely-different-secret-key-of-32-bytes!!", 60_000);

        String token = issuer.generateToken("jane@example.com", "Customer");

        assertThat(verifier.isValid(token)).isFalse();
    }
}
