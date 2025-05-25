package com.example.kafkapattern.order;

import java.util.List;

public record OrderRequest(
        List<OrderItemRequest> items
) {}
