package com.example.kafkapattern.order.saga;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentProcessCommand(
        UUID orderId,
        Long userId,
        Long paymentMethodId,
        BigDecimal amount
) {}
