package org.course.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.course.ecommerce.repository.AccountRepository;
import org.course.ecommerce.security.JwtService;
import org.course.ecommerce.support.TestAuthSupport;
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
class ProductControllerIT {

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

    private int createProduct(String name) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("productName", name, "unitPrice", 18.0));

        String location = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        return Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    void createReadUpdateDeleteRoundTrip() throws Exception {
        int id = createProduct("Chai");

        mockMvc.perform(get("/api/products/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Chai"));

        String updateBody = objectMapper.writeValueAsString(
                Map.of("productId", id, "productName", "Chai", "unitPrice", 20.0, "discontinued", true));

        mockMvc.perform(put("/api/products/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(updateBody))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.discontinued").value(true));

        mockMvc.perform(delete("/api/products/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRejectsBlankName() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("productName", ""));

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listIncludesCreatedProduct() throws Exception {
        createProduct("Aniseed Syrup");

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.productName == 'Aniseed Syrup')]").exists());
    }
}
