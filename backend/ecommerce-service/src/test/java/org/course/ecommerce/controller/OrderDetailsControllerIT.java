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
class OrderDetailsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtService jwtService;

    private String token;
    private int orderId;
    private int productId;

    @BeforeEach
    void setUp() throws Exception {
        token = TestAuthSupport.obtainToken(accountRepository, jwtService);

        // The find-by-id queries JOIN FETCH the product, so a real Order + Product
        // row must exist before an OrderDetails row referencing them can be read back.
        String orderLocation = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("shipName", "Test Order"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        orderId = Integer.parseInt(orderLocation.substring(orderLocation.lastIndexOf('/') + 1));

        String productLocation = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("productName", "Test Product"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        productId = Integer.parseInt(productLocation.substring(productLocation.lastIndexOf('/') + 1));
    }

    private Map<String, Object> detailBody(double unitPrice, int quantity) {
        return Map.of(
                "orderId", orderId,
                "productId", productId,
                "unitPrice", unitPrice,
                "quantity", quantity,
                "discount", 0.0f);
    }

    @Test
    void addGetUpdateDeleteRoundTrip() throws Exception {
        mockMvc.perform(post("/api/orderdetails")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(detailBody(18.0, 2))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/orderdetails/" + orderId + "/" + productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));

        mockMvc.perform(get("/api/orderdetails/" + orderId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(productId));

        mockMvc.perform(put("/api/orderdetails/" + orderId + "/" + productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(detailBody(18.0, 5))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orderdetails/" + orderId + "/" + productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.quantity").value(5));

        mockMvc.perform(delete("/api/orderdetails/" + orderId + "/" + productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orderdetails/" + orderId + "/" + productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void addingTheSameLineTwiceIsRejected() throws Exception {
        String body = objectMapper.writeValueAsString(detailBody(18.0, 2));

        mockMvc.perform(post("/api/orderdetails")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/orderdetails")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
