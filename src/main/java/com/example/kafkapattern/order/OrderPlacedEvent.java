package com.example.kafkapattern.order;

import java.util.List;

public record OrderPlacedEvent(
        Long orderId,
        Long userId,
        List<OrderItemDto> items
) {
    public static OrderPlacedEvent from(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new OrderPlacedEvent(
                order.getId(),
                order.getUserId(),
                itemDtos
        );
    }

}
