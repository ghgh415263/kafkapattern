package com.example.kafkapattern.order.saga;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreateSagaPayload(
        UUID orderId,
        Long userId,
        Long paymentMethodId,
        BigDecimal amount
) {}