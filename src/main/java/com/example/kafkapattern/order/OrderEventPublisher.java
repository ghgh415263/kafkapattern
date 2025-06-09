package com.example.kafkapattern.order;

import com.example.kafkapattern.ObjectSerializer;
import com.example.kafkapattern.event.DomainEventEnvelope;
import com.example.kafkapattern.event.OutboxEvent;
import com.example.kafkapattern.event.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectSerializer objectSerializer;

    public void publishEvent(Order order, OrderDomainEvent event) {

        DomainEventEnvelope<OrderDomainEvent> envelope = new DomainEventEnvelope<>(
                event.getClass().getSimpleName(),  // eventType
                "ORDER",                          // aggregateType
                order.getId().toString(),          // aggregateId
                event                             // payload
        );

        // Envelope 전체를 serialize → payload로 저장
        String envelopePayload = objectSerializer.serialize(envelope);

        // OutboxEvent 직접 구성
        OutboxEvent outboxEvent = new OutboxEvent(
                envelope.getEventId(),           // eventId (== OutboxEvent id)
                envelope.getAggregateType(),     // aggregateType
                envelope.getAggregateId(),       // aggregateId
                envelope.getEventType(),         // eventType
                envelopePayload                  // payload (Envelope 전체)
        );

        // Outbox 저장
        outboxEventRepository.save(outboxEvent);
    }
}
