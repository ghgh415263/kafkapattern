package com.example.kafkapattern.order;

public record OrderItemRequest(
        Long productId,
        int quantity
) {}
