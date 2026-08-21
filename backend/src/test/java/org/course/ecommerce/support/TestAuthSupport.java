package org.course.ecommerce.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Every controller other than /api/auth sits behind SecurityConfig's
 * anyRequest().authenticated(), so integration tests for them need a real bearer
 * token. This registers a fresh, throwaway Customer account through the actual
 * /api/auth flow (same path the frontend uses) rather than reaching into internals.
 */
public final class TestAuthSupport {

    private TestAuthSupport() {}

    public static String obtainToken(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        String email = "it-caller-" + System.nanoTime() + "@example.com";

        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(Map.of(
                        "companyName", "IT Caller Co",
                        "email", email,
                        "password", "supersecret1"))));

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", "supersecret1"))))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }
}
