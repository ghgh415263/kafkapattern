package com.example.kafkapattern.order;

import java.util.List;

public record OrderCreatedEvent(String orderId, Long userId, List<OrderItem> items) implements OrderDomainEvent {
}
