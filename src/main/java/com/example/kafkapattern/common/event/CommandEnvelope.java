package com.example.kafkapattern.common.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CommandEnvelope<T> {
    private final UUID commandId = UUID.randomUUID(); // 커맨드 고유 ID
    private final String commandType;      // 예: "PaymentProcessCommand"
    private final String targetAggregateId; // 커맨드가 특정 애그리거트를 대상으로 할 경우 (예: Order ID)
    private final T payload;               // 실제 커맨드 데이터 (예: PaymentProcessCommand 객체)
    private final UUID correlationId;    // Saga 추적을 위한 ID
    private final String eventSource = "ProductOrderService";
    private final long timestamp = System.currentTimeMillis(); // 커맨드 발행 시간

    public CommandEnvelope(String commandType, String targetAggregateId, T payload, UUID correlationId) {
        this.commandType = commandType;
        this.targetAggregateId = targetAggregateId;
        this.payload = payload;
        this.correlationId = correlationId;
    }
}
