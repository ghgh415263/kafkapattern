package com.example.kafkapattern.order;

import com.example.kafkapattern.event.ResultWithEvent;
import com.example.kafkapattern.order.event.OrderCommandPublisher;
import com.example.kafkapattern.order.event.PaymentProcessCommand;
import com.example.kafkapattern.product.Product;
import com.example.kafkapattern.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final OrderEventPublisher orderEventPublisher;
    private final OrderCommandPublisher orderCommandPublisher;

    @Transactional
    public String placeOrder(OrderRequest request) {

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
        ResultWithEvent<Order, OrderDomainEvent> orderWithEvent = Order.create(1l, orderItems);

        // 주문 저장
        orderRepository.save(orderWithEvent.result());

        orderEventPublisher.publishEvent(orderWithEvent.result(), orderWithEvent.event());

        PaymentProcessCommand paymentCommand = new PaymentProcessCommand(orderWithEvent.result().getId(), orderWithEvent.result().getTotalAmount());
        orderCommandPublisher.publishPaymentProcessCommand(paymentCommand, orderWithEvent.result().getId());

        return orderWithEvent.result().getId();
    }
}