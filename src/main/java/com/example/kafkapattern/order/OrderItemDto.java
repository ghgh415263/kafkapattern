package com.example.kafkapattern.order;

import java.math.BigDecimal;

public record OrderItemDto(
        Long productId,
        int quantity,
        BigDecimal price
) {}
