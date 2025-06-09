package com.example.kafkapattern.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPollingService {

    private final OutboxEventRepository outboxEventRepository;
    private final EventProducer eventProducer;

    // 5초마다 polling (프로덕션에서는 100~500ms 주기적으로 돌리기도 함)
    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void pollAndSendOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findTop10ByStatusOrderByCreatedAtAsc(OutboxEventStatus.INIT);

        for (OutboxEvent event : events) {
            try {
                // Kafka 전송
                eventProducer.send(
                        event.getTopic(),
                        event.getEventkey(),
                        event.getPayload()
                );

                // 상태 변경
                event.changeStatus(OutboxEventStatus.SUCCESS);
                log.info("Sent outbox event: {}", event.getId());
            } catch (Exception e) {
                event.changeStatus(OutboxEventStatus.FAIL);
                log.error("Failed to send outbox event: {}", event.getId(), e);
            }
        }
    }
}
