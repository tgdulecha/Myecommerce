package org.course.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.course.ecommerce.dto.LoginRequestDto;
import org.course.ecommerce.dto.RegisterRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Exercises the real Spring wiring (SecurityConfig, JwtAuthenticationFilter, JPA
 * repositories) against a dedicated NorthWind_clone database - see
 * src/test/resources/sql/northwind_clone_schema.sql for the schema this expects.
 * Rolled back per-test via @Transactional, so it never leaves data behind.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequestDto registerRequest(String companyName, String email) {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setCompanyName(companyName);
        request.setEmail(email);
        request.setPassword("supersecret1");
        return request;
    }

    @Test
    void registerThenLoginThenMeReturnsTheAccount() throws Exception {
        RegisterRequestDto register = registerRequest("Integration Test Co", "it-flow@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("it-flow@example.com"))
                .andExpect(jsonPath("$.role").value("Customer"))
                .andExpect(jsonPath("$.customerID").value("INTEG"));

        LoginRequestDto login = new LoginRequestDto();
        login.setEmail("it-flow@example.com");
        login.setPassword("supersecret1");

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.account.email").value("it-flow@example.com"))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("it-flow@example.com"));
    }

    @Test
    void loginWithWrongPasswordIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                registerRequest("Wrong Password Co", "it-badpass@example.com"))))
                .andExpect(status().isCreated());

        LoginRequestDto login = new LoginRequestDto();
        login.setEmail("it-badpass@example.com");
        login.setPassword("not-the-right-password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meWithoutATokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meWithAGarbageTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateEmailRegistrationIsRejected() throws Exception {
        RegisterRequestDto first = registerRequest("First Co", "it-dupe@example.com");
        RegisterRequestDto second = registerRequest("Second Co", "it-dupe@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isBadRequest());
    }
}
