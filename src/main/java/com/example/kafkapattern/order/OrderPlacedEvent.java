package com.example.kafkapattern.order;

public record OrderPlacedEvent (Long orderId, Long userId) {
}
