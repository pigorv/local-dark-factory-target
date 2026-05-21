package com.example.users;

import java.math.BigDecimal;

public record Order(long id, long userId, BigDecimal total, OrderStatus status) {
}
