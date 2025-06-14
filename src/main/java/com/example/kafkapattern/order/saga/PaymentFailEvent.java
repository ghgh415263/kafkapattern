package com.example.kafkapattern.order.saga;

import java.util.UUID;

public record PaymentFailEvent(UUID correlationId) {}
