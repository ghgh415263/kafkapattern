package com.example.kafkapattern.order;

public record OrderPlacedEvent (String eventId, Long orderId, String payload) {
}
