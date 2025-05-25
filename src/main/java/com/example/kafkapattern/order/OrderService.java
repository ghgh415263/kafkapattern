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

        // 데드락을 피하기 위한 sorting
        List<OrderItemRequest> sorted = request.items().stream()
                .sorted(Comparator.comparing(OrderItemRequest::productId))
                .toList();

        // 상품 조회 + 재고 감소 + OrderItem 생성
        List<OrderItem> orderItems = sorted.stream()
                .map(item -> {
                    Product product = productRepository.findByIdForUpdate(item.productId())
                            .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다"));

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