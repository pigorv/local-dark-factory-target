package com.example.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {

    private OrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new OrderRepository();
    }

    @Test
    void createAcceptsOrderAndReturnsItWithAllFieldsPopulated() {
        Order input = new Order(0, 100L, BigDecimal.ZERO, OrderStatus.PENDING);

        Order result = repository.create(input);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals(100L, result.userId());
        assertNotNull(result.total());
        assertEquals(OrderStatus.PENDING, result.status());
    }

    @Test
    void findByIdReturnsMatchingOrderWhenIdExists() {
        Order created = repository.create(new Order(0, 100L, BigDecimal.ZERO, OrderStatus.PENDING));

        Order result = repository.findById(created.id());

        assertEquals(created, result);
    }

    @Test
    void findByIdReturnsNullWhenIdDoesNotExist() {
        Order result = repository.findById(999L);

        assertNull(result);
    }

    @Test
    void findAllReturnsAllStoredOrders() {
        Order order1 = repository.create(new Order(0, 100L, BigDecimal.ZERO, OrderStatus.PENDING));
        Order order2 = repository.create(new Order(0, 200L, BigDecimal.ZERO, OrderStatus.COMPLETED));
        Order order3 = repository.create(new Order(0, 300L, BigDecimal.ZERO, OrderStatus.FAILED));

        List<Order> result = repository.findAll();

        assertEquals(3, result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(order2));
        assertTrue(result.contains(order3));
    }

    @Test
    void updateModifiesStoredOrderAndReturnsUpdatedVersion() {
        Order created = repository.create(new Order(0, 100L, BigDecimal.ZERO, OrderStatus.PENDING));
        Order updateRequest = new Order(created.id(), 200L, BigDecimal.ZERO, OrderStatus.COMPLETED);

        Order result = repository.update(updateRequest);

        assertNotNull(result);
        assertEquals(created.id(), result.id());
        assertEquals(200L, result.userId());
        assertEquals(OrderStatus.COMPLETED, result.status());

        Order retrieved = repository.findById(created.id());
        assertEquals(result, retrieved);
    }

    @Test
    void deleteRemovesOrderFromStorage() {
        Order created = repository.create(new Order(0, 100L, BigDecimal.ZERO, OrderStatus.PENDING));

        repository.delete(created.id());

        Order result = repository.findById(created.id());
        assertNull(result);
    }

    @Test
    void totalEqualsTwentyPlusIdForAnyOrder() {
        Order order1 = repository.create(new Order(0, 100L, BigDecimal.ZERO, OrderStatus.PENDING));
        assertEquals(BigDecimal.valueOf(20 + order1.id()), order1.total());

        Order order2 = repository.create(new Order(0, 200L, BigDecimal.ZERO, OrderStatus.COMPLETED));
        assertEquals(BigDecimal.valueOf(20 + order2.id()), order2.total());

        Order order3 = repository.create(new Order(0, 300L, BigDecimal.ZERO, OrderStatus.FAILED));
        assertEquals(BigDecimal.valueOf(20 + order3.id()), order3.total());
    }

    @Test
    void createAssignsMonotonicallyIncreasingIdsStartingFromOne() {
        Order order1 = repository.create(new Order(0, 100L, BigDecimal.ZERO, OrderStatus.PENDING));
        assertEquals(1, order1.id());

        Order order2 = repository.create(new Order(0, 200L, BigDecimal.ZERO, OrderStatus.COMPLETED));
        assertEquals(2, order2.id());

        Order order3 = repository.create(new Order(0, 300L, BigDecimal.ZERO, OrderStatus.FAILED));
        assertEquals(3, order3.id());
    }

    @Test
    void updateMaintainsTotalCalculationInvariant() {
        Order created = repository.create(new Order(0, 100L, BigDecimal.ZERO, OrderStatus.PENDING));
        Order updateRequest = new Order(created.id(), 200L, BigDecimal.ZERO, OrderStatus.COMPLETED);

        Order updated = repository.update(updateRequest);

        assertEquals(BigDecimal.valueOf(20 + updated.id()), updated.total());
    }
}
