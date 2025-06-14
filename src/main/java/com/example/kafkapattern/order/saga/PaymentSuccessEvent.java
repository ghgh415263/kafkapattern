package com.example.kafkapattern.order.saga;

import java.util.UUID;

public record PaymentSuccessEvent(UUID correlationId) {}
