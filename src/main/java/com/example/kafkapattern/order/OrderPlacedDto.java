package com.example.kafkapattern.order;

import java.util.List;

public record OrderPlacedDto(
        String orderId,
        Long userId,
        List<OrderItemDto> items
) {
    public static OrderPlacedDto from(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new OrderPlacedDto(
                order.getId(),
                order.getUserId(),
                itemDtos
        );
    }

}
