package com.example.kafkapattern.event;

import com.example.kafkapattern.order.OrderPlacedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        // 이벤트 처리 로직
        System.out.println("주문 완료 이벤트 받음: " + event.orderId());
        // 예: Outbox 저장, 알림 발송 등
    }
}
