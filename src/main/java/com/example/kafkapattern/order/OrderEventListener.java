package com.example.kafkapattern.order;

import com.example.kafkapattern.event.OutboxEvent;
import com.example.kafkapattern.event.OutboxEventService;
import com.example.kafkapattern.event.OutboxEventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.example.kafkapattern.event.AsyncConfig.EVENT_ASYNC_TASK_EXECUTOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final OutboxEventService outboxEventService;

    @Async(EVENT_ASYNC_TASK_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlacedEvent(OutboxEvent event) {
        // 이벤트 처리 로직
        log.info("주문 완료 이벤트 받음: " + event.getAggregateId());
        log.info("현재 쓰레드 이름 in OrderEventListener: " + Thread.currentThread().getName());

        kafkaTemplate.send("order-events", event.getAggregateId().toString(), event.getPayload())
                .thenAccept(result -> {
                    log.info("Kafka 전송 성공 - offset: {}", result.getRecordMetadata().offset());
                    outboxEventService.updateStatus(event.getId(), OutboxEventStatus.SUCCESS);
                })
                .exceptionally(ex -> {
                    log.error("Kafka 전송 실패 - error: {}", ex.getMessage(), ex);
                    try {
                        outboxEventService.updateStatus(event.getId(), OutboxEventStatus.FAIL);
                    } catch (Exception e) {
                        log.error("Outbox 상태 업데이트 실패: {}", e.getMessage(), e);
                    }
                    return null;
                });
    }
}
