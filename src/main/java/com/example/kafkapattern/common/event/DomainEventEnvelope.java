package com.example.kafkapattern.common.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DomainEventEnvelope<T extends DomainEvent> {
    private final String eventId = UUID.randomUUID().toString();
    private final String eventType;        // 예: "OrderCreatedEvent"
    private final String aggregateType;    // 예: "ORDER"
    private final String aggregateId;      // 예: 주문 ID
    private final T payload;               // 실제 도메인 이벤트
    private String eventSource = "ProductOrderService";
    private final long timestamp = System.currentTimeMillis();  // 발생 시간

    public DomainEventEnvelope(String eventType, String aggregateType, String aggregateId, T payload) {
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
    }
}
