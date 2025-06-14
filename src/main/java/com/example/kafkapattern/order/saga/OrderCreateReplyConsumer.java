package com.example.kafkapattern.order.saga;

import com.example.kafkapattern.common.saga.SagaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@KafkaListener(topics = "order-create-reply", groupId = "order-service")
public class OrderCreateReplyConsumer {

    private final SagaManager<OrderCreateSagaPayload> sagaManager;

    @KafkaHandler
    public void handle(PaymentSuccessEvent event) {
        log.info("Received PaymentSuccessEvent: {}", event);
    }

    @KafkaHandler
    public void handle(PaymentFailEvent event) {
        log.info("Received PaymentFailEvent: {}", event);
    }
}
