package com.example.kafkapattern.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private int quantity;

    private BigDecimal price; // 단일 상품 가격 (1개 가격)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public BigDecimal getItemPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // 연관관계 편의 메서드 - 단순히 필드만 세팅
    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderItem(Long productId, int quantity, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
}
