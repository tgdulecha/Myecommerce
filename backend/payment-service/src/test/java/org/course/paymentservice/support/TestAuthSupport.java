package org.course.paymentservice.support;

import org.course.paymentservice.entity.Account;
import org.course.paymentservice.repository.AccountRepository;
import org.course.paymentservice.security.JwtService;

import java.time.LocalDateTime;

/**
 * Every controller here sits behind SecurityConfig's anyRequest().authenticated(),
 * so integration tests need a real bearer token. Since /api/auth lives in
 * auth-service - a separate deployable this test context doesn't start - we can't
 * get one through MockMvc. Instead this seeds a throwaway Account row directly (the
 * same row auth-service's /api/auth/register would have created) and mints a token
 * with the same JwtService/secret auth-service uses, since a JWT is self-verifying
 * and doesn't require calling the issuer back.
 */
public final class TestAuthSupport {

    private TestAuthSupport() {}

    public static String obtainToken(AccountRepository accountRepository, JwtService jwtService) {
        String email = "it-caller-" + System.nanoTime() + "@example.com";

        Account account = new Account();
        account.setEmail(email);
        account.setPassword("not-checked-by-this-service");
        account.setRole("Customer");
        account.setVerified(true);
        account.setCreatedAt(LocalDateTime.now());
        accountRepository.save(account);

        return jwtService.generateToken(email, "Customer");
    }
}
