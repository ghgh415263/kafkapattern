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
public class OrderCommandPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectSerializer objectSerializer;

    private static final String PAYMENT_COMMAND_KAFKA_TOPIC = "payment-commands"; // 실제 Kafka 토픽명

    /**
     * 결제 처리 커맨드를 CommandEnvelope에 담아 아웃박스에 저장합니다.
     * @param command 발행할 PaymentProcessCommand 객체 (페이로드)
     * @param correlationId Saga 추적을 위한 ID (보통 주문 ID)
     */
    public void publishPaymentProcessCommand(PaymentProcessCommand command, String correlationId) {
        // CommandEnvelope 생성
        CommandEnvelope<PaymentProcessCommand> commandEnvelope = new CommandEnvelope<>(
                "PaymentProcessCommand", // commandType
                command.orderId(),    // targetAggregateId (Order ID)
                command,                 // payload
                correlationId            // correlationId
        );

        // CommandEnvelope 전체를 serialize → payload로 저장
        String envelopePayload = objectSerializer.serialize(commandEnvelope);

        // OutboxEvent를 커맨드 정보로 구성
        OutboxEvent outboxCommandEvent = new OutboxEvent(
                commandEnvelope.getCommandId(),            // id (커맨드 ID 사용)
                PAYMENT_COMMAND_KAFKA_TOPIC,               // topic (실제 보낼 Kafka 토픽명)
                commandEnvelope.getTargetAggregateId(),    // eventKey (Kafka 메시지 키로 사용할 주문 ID)
                commandEnvelope.getCommandType(),          // eventType (커맨드 타입)
                envelopePayload                            // payload (직렬화된 CommandEnvelope)
        );

        outboxEventRepository.save(outboxCommandEvent);
    }
}
