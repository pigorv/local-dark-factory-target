package com.example.users;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void orderRecordHasFourComponentsInCorrectOrder() {
        Order order = new Order(1L, 100L, BigDecimal.valueOf(21), OrderStatus.PENDING);

        assertEquals(1L, order.id());
        assertEquals(100L, order.userId());
        assertEquals(BigDecimal.valueOf(21), order.total());
        assertEquals(OrderStatus.PENDING, order.status());
    }

    @Test
    void orderStatusEnumHasExactlyThreeValues() {
        OrderStatus[] values = OrderStatus.values();

        assertEquals(3, values.length);
        assertEquals(OrderStatus.PENDING, values[0]);
        assertEquals(OrderStatus.COMPLETED, values[1]);
        assertEquals(OrderStatus.FAILED, values[2]);
    }

    @Test
    void orderTotalEqualsExpectedFormulaForVariousIds() {
        Order order1 = new Order(1L, 100L, BigDecimal.valueOf(21), OrderStatus.PENDING);
        assertEquals(BigDecimal.valueOf(21), order1.total());
        assertEquals(BigDecimal.valueOf(20 + 1), order1.total());

        Order order5 = new Order(5L, 100L, BigDecimal.valueOf(25), OrderStatus.PENDING);
        assertEquals(BigDecimal.valueOf(25), order5.total());
        assertEquals(BigDecimal.valueOf(20 + 5), order5.total());

        Order order50 = new Order(50L, 100L, BigDecimal.valueOf(70), OrderStatus.PENDING);
        assertEquals(BigDecimal.valueOf(70), order50.total());
        assertEquals(BigDecimal.valueOf(20 + 50), order50.total());
    }
}
