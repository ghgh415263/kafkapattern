package com.example.kafkapattern.order;

import com.example.kafkapattern.ObjectSerializer;
import com.example.kafkapattern.event.OutboxEvent;
import com.example.kafkapattern.event.OutboxEventRepository;
import com.example.kafkapattern.product.Product;
import com.example.kafkapattern.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectSerializer objectSerializer;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long placeOrder(OrderRequest request) {

        log.info("현재 쓰레드 이름 in OrderService: " + Thread.currentThread().getName());

        List<OrderItemRequest> items = request.items();

        // productId 리스트 추출
        List<Long> productIds = items.stream()
                .map(OrderItemRequest::productId)
                .toList();

        // IN절로 조회 + 락
        List<Product> products = productRepository.findByIdInForUpdate(productIds);

        // Map 변환
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 처리
        List<OrderItem> orderItems = items.stream()
                .map(item -> {
                    Product product = productMap.get(item.productId());
                    if (product == null) {
                        throw new IllegalArgumentException("상품이 존재하지 않습니다");
                    }

                    product.decreaseStock(item.quantity());

                    return new OrderItem(
                            item.productId(),
                            item.quantity(),
                            product.getPrice()
                    );
                })
                .toList();

        // 주문 생성
        Order order = new Order(1l);

        // order와 orderItems 연관관계 설정
        orderItems.forEach(order::addItem);

        // 주문 저장
        orderRepository.save(order);

        OutboxEvent outboxEvent = new OutboxEvent("ORDER", order.getId(), "ORDER_PLACED", objectSerializer.serialize(OrderPlacedDto.from(order)));
        outboxEventRepository.save(outboxEvent);
        eventPublisher.publishEvent(new OrderPlacedEvent(outboxEvent.getId(), outboxEvent.getAggregateId(), outboxEvent.getPayload()));

        return order.getId();
    }
}