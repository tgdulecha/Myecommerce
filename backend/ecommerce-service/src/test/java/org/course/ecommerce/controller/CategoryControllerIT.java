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
class CategoryControllerIT {

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
        mockMvc.perform(get("/api/category"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createReadUpdateDeleteRoundTrip() throws Exception {
        String createBody = objectMapper.writeValueAsString(
                Map.of("categoryName", "Beverages", "description", "Soft drinks, coffees, teas"));

        String createResponse = mockMvc.perform(post("/api/category")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // POST returns 201/Location only (body may be empty per the controller), so
        // fetch the list to find the id we just created.
        String listResponse = mockMvc.perform(get("/api/category")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var categories = objectMapper.readTree(listResponse);
        int id = -1;
        for (var node : categories) {
            if ("Beverages".equals(node.get("categoryName").asText())) {
                id = node.get("categoryID").asInt();
            }
        }
        org.junit.jupiter.api.Assertions.assertNotEquals(-1, id, "created category not found in list");

        mockMvc.perform(get("/api/category/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Beverages"));

        String updateBody = objectMapper.writeValueAsString(
                Map.of("categoryID", id, "categoryName", "Beverages", "description", "Updated description"));

        mockMvc.perform(put("/api/category/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(updateBody))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/category/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.description").value("Updated description"));

        mockMvc.perform(delete("/api/category/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/category/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRejectsBlankName() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("categoryName", "", "description", "x"));

        mockMvc.perform(post("/api/category")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
