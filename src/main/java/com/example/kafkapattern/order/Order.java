package com.example.kafkapattern.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "ORDERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order(Long userId, List<OrderItem> items) {
        this.userId = userId;
        for (OrderItem item : items) {
            addItem(item);
        }
    }

    // 연관관계 편의 메서드
    private void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
