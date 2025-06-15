package com.example.kafkapattern.order;

import com.example.kafkapattern.common.event.ResultWithEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "ORDERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderState orderState = OrderState.PENDING;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    // 생성자 (id 자동 생성)
    private Order(Long userId, List<OrderItem> items) {
        this.userId = userId;
        for (OrderItem item : items) {
            addItem(item);
        }
    }

    private void addItem(OrderItem item) {
        items.add(item);
    }

    // 정적 팩토리 메서드 + 이벤트 생성
    public static ResultWithEvent<Order, OrderDomainEvent> create(Long userId, List<OrderItem> items) {
        Order order = new Order(userId, items);

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                userId,
                items
        );

        return new ResultWithEvent<>(order, event);
    }

    public BigDecimal getTotalAmount() {
        return this.items.stream()
                .map(OrderItem::getItemTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
