package com.example.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void createReturnsCreatedStatusWithGeneratedId() throws Exception {
        String orderJson = """
            {
                "id": 0,
                "userId": 123,
                "total": 0,
                "status": "PENDING"
            }
            """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.userId").value(123))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getByIdReturnsOkWhenOrderExists() throws Exception {
        // Create an order first
        String orderJson = """
            {
                "id": 0,
                "userId": 456,
                "total": 0,
                "status": "COMPLETED"
            }
            """;

        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Extract the id from the response
        long createdId = Long.parseLong(response.split("\"id\":")[1].split(",")[0]);

        mockMvc.perform(get("/api/orders/" + createdId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(createdId))
            .andExpect(jsonPath("$.userId").value(456))
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void getByIdReturnsNotFoundWhenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/orders/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAllReturnsOkWithJsonArray() throws Exception {
        // Create a couple of orders first
        String order1Json = """
            {
                "id": 0,
                "userId": 111,
                "total": 0,
                "status": "PENDING"
            }
            """;

        String order2Json = """
            {
                "id": 0,
                "userId": 222,
                "total": 0,
                "status": "FAILED"
            }
            """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(order1Json));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(order2Json));

        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void updateReturnsOkWithUpdatedOrder() throws Exception {
        // Create an order first
        String createJson = """
            {
                "id": 0,
                "userId": 333,
                "total": 0,
                "status": "PENDING"
            }
            """;

        String createResponse = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andReturn()
            .getResponse()
            .getContentAsString();

        long createdId = Long.parseLong(createResponse.split("\"id\":")[1].split(",")[0]);

        String updateJson = """
            {
                "id": %d,
                "userId": 333,
                "total": 100.50,
                "status": "COMPLETED"
            }
            """.formatted(createdId);

        mockMvc.perform(put("/api/orders/" + createdId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(createdId))
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        // Create an order first
        String orderJson = """
            {
                "id": 0,
                "userId": 444,
                "total": 0,
                "status": "PENDING"
            }
            """;

        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
            .andReturn()
            .getResponse()
            .getContentAsString();

        long createdId = Long.parseLong(response.split("\"id\":")[1].split(",")[0]);

        mockMvc.perform(delete("/api/orders/" + createdId))
            .andExpect(status().isNoContent());
    }
}
