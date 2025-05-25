package com.example.kafkapattern.product;

import com.example.kafkapattern.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private int stockQuantity;

    public Product(String name, BigDecimal price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // === 비즈니스 로직 ===
    public void decreaseStock(int quantity) {
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("재고 부족: 남은 수량 = " + stockQuantity);
        }
        this.stockQuantity -= quantity;
    }
}
