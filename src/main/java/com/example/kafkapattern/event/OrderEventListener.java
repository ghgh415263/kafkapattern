package com.example.kafkapattern.event;

import com.example.kafkapattern.order.OrderPlacedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.example.kafkapattern.event.AsyncConfig.EVENT_ASYNC_TASK_EXECUTOR;

@Component
public class OrderEventListener {

    @Async(EVENT_ASYNC_TASK_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        // 이벤트 처리 로직
        System.out.println("주문 완료 이벤트 받음: " + event.orderId());
        System.out.println("현재 쓰레드 이름 in OrderEventListener: " + Thread.currentThread().getName());
        // 예: Outbox 저장, 알림 발송 등
    }
}
