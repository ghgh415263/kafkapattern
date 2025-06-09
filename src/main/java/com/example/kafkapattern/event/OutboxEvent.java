package com.example.kafkapattern.event;

import com.example.kafkapattern.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "outbox_event", indexes = {
        @Index(name = "idx_outbox_status_createdat", columnList = "status, createdAt")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent extends BaseEntity {

    @Id
    private String id;
    private String topic; // 예: "ORDER"
    private String eventkey; // "주문id"
    private String eventType; // 예: "ORDER_PLACED"

    @Lob
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status = OutboxEventStatus.INIT;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime sentAt;

    public OutboxEvent(String id, String topic, String eventkey, String eventType, String payload) {
        this.id = id;
        this.topic = topic;
        this.eventkey = eventkey;
        this.eventType = eventType;
        this.payload = payload;
    }

    public void changeStatus(OutboxEventStatus newStatus) {
        this.status = newStatus;
        if (newStatus == OutboxEventStatus.SUCCESS) {
            this.sentAt = LocalDateTime.now();
        }
    }
}