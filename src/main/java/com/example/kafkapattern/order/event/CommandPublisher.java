package com.example.kafkapattern.order.event;

import com.example.kafkapattern.ObjectSerializer;
import com.example.kafkapattern.common.event.CommandEnvelope;
import com.example.kafkapattern.common.event.OutboxEvent;
import com.example.kafkapattern.common.event.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectSerializer objectSerializer;

    /**
     * 커맨드 객체를 CommandEnvelope에 감싸고 OutboxEvent로 저장합니다.
     *
     * @param topic Kafka 토픽 이름
     * @param commandType 커맨드 타입명 (ex. "PaymentProcessCommand")
     * @param targetAggregateId 메시지 키로 사용할 대상 식별자 (ex. 주문 ID)
     * @param command 실제 커맨드 페이로드 객체
     * @param correlationId Saga 추적용 ID
     */
    public <T> void publish(String topic, String commandType, String targetAggregateId, T command, String correlationId) {
        // 커맨드 래핑
        CommandEnvelope<T> envelope = new CommandEnvelope<>(
                commandType,
                targetAggregateId,
                command,
                correlationId
        );

        // 직렬화
        String serializedPayload = objectSerializer.serialize(envelope);

        // 아웃박스 이벤트 생성
        OutboxEvent outboxEvent = new OutboxEvent(
                envelope.getCommandId(),
                topic,
                targetAggregateId,
                commandType,
                serializedPayload
        );

        // DB 저장 (트랜잭션 내에서 커밋됨)
        outboxEventRepository.save(outboxEvent);

        log.info("[CommandPublisher] Saved command to outbox: type={}, aggregateId={}, topic={}",
                commandType, targetAggregateId, topic);
    }
}