package com.example.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void listReturnsFirstPage() throws Exception {
        mockMvc.perform(get("/api/users").param("offset", "0").param("limit", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(5))
            .andExpect(jsonPath("$.items[0].id").value(1))
            .andExpect(jsonPath("$.total").value(50));
    }

    @Test
    void listReturnsSecondPage() throws Exception {
        mockMvc.perform(get("/api/users").param("offset", "5").param("limit", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(5))
            .andExpect(jsonPath("$.items[0].id").value(6));
    }

    @Test
    void cursorPaginationReturnsFirstPage() throws Exception {
        mockMvc.perform(get("/api/users").param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(10))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[9].id").value(10))
            .andExpect(jsonPath("$.nextCursor").value("10"));
    }

    @Test
    void cursorPaginationContinuesWithCursor() throws Exception {
        mockMvc.perform(get("/api/users").param("cursor", "10").param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(10))
            .andExpect(jsonPath("$.data[0].id").value(11))
            .andExpect(jsonPath("$.data[9].id").value(20))
            .andExpect(jsonPath("$.nextCursor").value("20"));
    }

    @Test
    void cursorPaginationDetectsEndOfResults() throws Exception {
        // Request from cursor 45 with limit 10, should get only 5 items (46-50)
        mockMvc.perform(get("/api/users").param("cursor", "45").param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(5))
            .andExpect(jsonPath("$.data[0].id").value(46))
            .andExpect(jsonPath("$.data[4].id").value(50))
            .andExpect(jsonPath("$.nextCursor").value("50"));
    }

    @Test
    void cursorPaginationReturnsEmptyWhenNoMoreResults() throws Exception {
        // Request from cursor 50 (last ID), should get empty list
        mockMvc.perform(get("/api/users").param("cursor", "50").param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0))
            .andExpect(jsonPath("$.nextCursor").doesNotExist());
    }

    @Test
    void cursorPaginationHandlesCustomLimit() throws Exception {
        mockMvc.perform(get("/api/users").param("limit", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(3))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[1].id").value(2))
            .andExpect(jsonPath("$.data[2].id").value(3))
            .andExpect(jsonPath("$.nextCursor").value("3"));
    }

    @Test
    void cursorPaginationHandlesInvalidCursor() throws Exception {
        // Invalid cursor should be treated as null and start from beginning
        mockMvc.perform(get("/api/users").param("cursor", "invalid").param("limit", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(5))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.nextCursor").value("5"));
    }

    @Test
    void cursorPaginationMultiPageScenario() throws Exception {
        // Simulate a multi-page scenario: page 1, page 2, page 3
        MvcResult page1 = mockMvc.perform(get("/api/users").param("limit", "15"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(15))
            .andExpect(jsonPath("$.nextCursor").value("15"))
            .andReturn();

        MvcResult page2 = mockMvc.perform(get("/api/users").param("cursor", "15").param("limit", "15"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(15))
            .andExpect(jsonPath("$.data[0].id").value(16))
            .andExpect(jsonPath("$.nextCursor").value("30"))
            .andReturn();

        MvcResult page3 = mockMvc.perform(get("/api/users").param("cursor", "30").param("limit", "15"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(15))
            .andExpect(jsonPath("$.data[0].id").value(31))
            .andExpect(jsonPath("$.nextCursor").value("45"))
            .andReturn();

        // Page 4 should have remaining 5 items
        mockMvc.perform(get("/api/users").param("cursor", "45").param("limit", "15"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(5))
            .andExpect(jsonPath("$.data[0].id").value(46))
            .andExpect(jsonPath("$.data[4].id").value(50))
            .andExpect(jsonPath("$.nextCursor").value("50"));
    }
}
