package com.example.kafkapattern.order;

import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(UUID orderId, Long userId, List<OrderItem> items) implements OrderDomainEvent {
}
