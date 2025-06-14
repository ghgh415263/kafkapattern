package com.example.kafkapattern.order.saga;

import java.math.BigDecimal;

public record OrderCreateSagaPayload(
        String orderId,
        Long userId,
        Long paymentMethodId,
        BigDecimal amount
) {}