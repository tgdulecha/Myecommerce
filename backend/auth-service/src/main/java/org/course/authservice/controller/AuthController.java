package org.course.authservice.controller;

import org.course.authservice.dto.AccountDto;
import org.course.authservice.dto.AuthResponseDto;
import org.course.authservice.dto.LoginRequestDto;
import org.course.authservice.dto.RegisterRequestDto;
import org.course.authservice.security.JwtService;
import org.course.authservice.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// URL: http://localhost:8081/api/auth
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AccountService accountService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AccountDto> register(@RequestBody RegisterRequestDto request) {
        AccountDto created = accountService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        // Throws BadCredentialsException (-> 401 via GlobalExceptionHandler) on a bad email/password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        accountService.recordLogin(request.getEmail());
        AccountDto account = accountService.getByEmail(request.getEmail());
        String token = jwtService.generateToken(account.getEmail(), account.getRole());

        return ResponseEntity.ok(new AuthResponseDto(token, account));
    }

    @GetMapping("/me")
    public ResponseEntity<AccountDto> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(accountService.getByEmail(authentication.getName()));
    }
}
