package com.example.kafkapattern;

import com.example.kafkapattern.product.Product;
import com.example.kafkapattern.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductInitRunner implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        productRepository.saveAll(List.of(
                new Product("맥북", new BigDecimal("2200000"), 10),
                new Product("에어팟", new BigDecimal("350000"), 50),
                new Product("아이폰", new BigDecimal("1400000"), 30)
        ));
        System.out.println("products initialized.");
    }
}
