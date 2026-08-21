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
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void authenticate() throws Exception {
        token = TestAuthSupport.obtainToken(mockMvc, objectMapper);
    }

    private int createOrder(String shipName) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("shipName", shipName, "shipCountry", "France"));

        String location = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        return Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    void createReadUpdateDeleteRoundTrip() throws Exception {
        int id = createOrder("Around the Horn");

        mockMvc.perform(get("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipName").value("Around the Horn"));

        String updateBody = objectMapper.writeValueAsString(
                Map.of("orderId", id, "shipName", "Around the Horn", "shipCountry", "UK"));

        mockMvc.perform(put("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(updateBody))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.shipCountry").value("UK"));

        mockMvc.perform(delete("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void pagedListReturnsCreatedOrder() throws Exception {
        createOrder("Bottom-Dollar Markets");

        mockMvc.perform(get("/api/orders?page=1&size=50")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.shipName == 'Bottom-Dollar Markets')]").exists())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.pageSize").value(50));
    }

    @Test
    void pagedListRejectsNonPositivePageOrSize() throws Exception {
        mockMvc.perform(get("/api/orders?page=0&size=5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOfUnknownIdReturnsNotFound() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("orderId", 999999, "shipName", "Ghost"));

        mockMvc.perform(put("/api/orders/999999")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isNotFound());
    }
}
