package com.example.kafkapattern.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent {

    @Id
    private String id;
    private String aggregateType; // 예: "ORDER"
    private Long aggregateId; // "주문id"
    private String eventType; // 예: "ORDER_PLACED"

    @Lob
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    public OutboxEvent(String aggregateType, Long aggregateId, String eventType, String payload) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;

        this.id = UUID.randomUUID().toString();
        this.status = OutboxEventStatus.INIT;
        this.createdAt = LocalDateTime.now();
    }
}