package com.example.users;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {
    private final List<Order> orders = new ArrayList<>();
    private long currentId = 0;

    public Order create(Order order) {
        long newId = ++currentId;
        BigDecimal total = BigDecimal.valueOf(20 + newId);
        Order newOrder = new Order(newId, order.userId(), total, order.status());
        orders.add(newOrder);
        return newOrder;
    }

    public Order findById(long id) {
        return orders.stream()
            .filter(order -> order.id() == id)
            .findFirst()
            .orElse(null);
    }

    public List<Order> findAll() {
        return List.copyOf(orders);
    }

    public Order update(Order order) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).id() == order.id()) {
                BigDecimal total = BigDecimal.valueOf(20 + order.id());
                Order updated = new Order(order.id(), order.userId(), total, order.status());
                orders.set(i, updated);
                return updated;
            }
        }
        return null;
    }

    public void delete(long id) {
        orders.removeIf(order -> order.id() == id);
    }
}
