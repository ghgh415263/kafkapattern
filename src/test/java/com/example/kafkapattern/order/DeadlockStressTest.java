package com.example.kafkapattern.order;

import com.example.kafkapattern.product.Product;
import com.example.kafkapattern.product.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@SpringBootTest
public class DeadlockStressTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    private List<Long> productIds;

    @BeforeEach
    public void setup() {
        productRepository.deleteAll();

        // 상품 30개 저장
        productIds = IntStream.rangeClosed(1, 50)
                .mapToObj(i ->
                    productRepository.save(new Product("product"+ i, new BigDecimal("2200000"), 100000))
                )
                .map(Product::getId)
                .toList();
    }

    @Test
    public void testDeadlockStressWith100Threads() throws InterruptedException {
        int threadCount = 50;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    System.out.println("Thread-" + threadNum + " 주문 시도");

                    List<OrderItemRequest> items;
                    if (threadNum % 2 == 0) {
                        // 짝수 thread는 순서
                        items = productIds.stream()
                                .map(id -> new OrderItemRequest(id, 1))
                                .toList();
                    } else {
                        // 홀수 thread는 역순
                        List<Long> reversed = new ArrayList<>(productIds);
                        Collections.reverse(reversed);
                        items = reversed.stream()
                                .map(id -> new OrderItemRequest(id, 1))
                                .toList();
                    }

                    orderService.placeOrder(new OrderRequest(items));

                    System.out.println("Thread-" + threadNum + " 주문 성공");
                } catch (Exception e) {
                    System.out.println("Thread-" + threadNum + " 주문 실패 - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 완료 대기
        latch.await();
        executor.shutdown();

        System.out.println("100개 스레드 stress 테스트 종료");

        // 재고 확인
        List<Product> products = productRepository.findAll();

        int initialStock = 100000; // 초기 재고
        int expectedDecrease = threadCount; // 각 상품당 기대 감소량

        for (Product product : products) {
            int actualStock = product.getStockQuantity();
            int decreasedAmount = initialStock - actualStock;

            System.out.printf("Product %d (%s) - Decreased: %d - Remaining stock: %d%n",
                    product.getId(), product.getName(), decreasedAmount, actualStock);

            // 검증
            Assertions.assertEquals(expectedDecrease, decreasedAmount,
                    String.format("Product %d (%s) 의 감소량이 예상과 다름!", product.getId(), product.getName()));
        }

        System.out.println("=== 재고 확인 완료 ===");

    }
}
