package org.course.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class EmployeeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void authenticate() throws Exception {
        token = TestAuthSupport.obtainToken(mockMvc, objectMapper);
    }

    @Test
    void createReadUpdateDeleteRoundTrip() throws Exception {
        String createBody = objectMapper.writeValueAsString(
                Map.of("firstName", "Nancy", "lastName", "Davolio"));

        String location = mockMvc.perform(post("/api/employee")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        String id = location.substring(location.lastIndexOf('/') + 1);

        mockMvc.perform(get("/api/employee/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Nancy"))
                .andExpect(jsonPath("$.lastName").value("Davolio"));

        String updateBody = objectMapper.writeValueAsString(
                Map.of("employeeId", Integer.parseInt(id), "firstName", "Nancy", "lastName", "Weaver"));

        mockMvc.perform(put("/api/employee/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(updateBody))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/employee/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.lastName").value("Weaver"));

        mockMvc.perform(delete("/api/employee/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/employee/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRejectsMissingLastName() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("firstName", "Nancy"));

        // EmployeeServiceImpl.validate() throws IllegalArgumentException, which
        // GlobalExceptionHandler's @RestControllerAdvice maps to 400 for every controller.
        mockMvc.perform(post("/api/employee")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteOfUnknownIdReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/employee/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
