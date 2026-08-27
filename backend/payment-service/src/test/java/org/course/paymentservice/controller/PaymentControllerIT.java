package org.course.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.course.paymentservice.repository.AccountRepository;
import org.course.paymentservice.security.JwtService;
import org.course.paymentservice.support.TestAuthSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtService jwtService;

    private String token;

    @BeforeEach
    void authenticate() {
        token = TestAuthSupport.obtainToken(accountRepository, jwtService);
    }

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createRejectsZeroAmount() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("orderId", 10248, "customerEmail", "jane@example.com", "amount", 0, "method", "CreditCard"));

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createThenTransitionToCompletedRoundTrip() throws Exception {
        String createBody = objectMapper.writeValueAsString(
                Map.of("orderId", 10248, "customerEmail", "jane@example.com", "amount", 49.99, "method", "CreditCard"));

        String createResponse = mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        int id = objectMapper.readTree(createResponse).get("paymentId").asInt();

        mockMvc.perform(get("/api/payments/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(10248));

        mockMvc.perform(get("/api/payments").queryParam("orderId", "10248")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(10248));

        String statusBody = objectMapper.writeValueAsString(Map.of("status", "COMPLETED"));

        mockMvc.perform(patch("/api/payments/" + id + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(statusBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void updateStatusRejectsUnknownValue() throws Exception {
        String createBody = objectMapper.writeValueAsString(
                Map.of("orderId", 10249, "customerEmail", "jane@example.com", "amount", 10, "method", "PayPal"));

        String createResponse = mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int id = objectMapper.readTree(createResponse).get("paymentId").asInt();
        String statusBody = objectMapper.writeValueAsString(Map.of("status", "SHIPPED"));

        mockMvc.perform(patch("/api/payments/" + id + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(statusBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByIdReturns404WhenMissing() throws Exception {
        mockMvc.perform(get("/api/payments/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
