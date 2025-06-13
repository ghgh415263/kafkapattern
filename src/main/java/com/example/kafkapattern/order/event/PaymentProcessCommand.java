package com.example.kafkapattern.order.event;

import java.math.BigDecimal;

public record PaymentProcessCommand(
        long userId,
        long paymentMethodId,
        String orderId,
        BigDecimal totalAmount
) {
}