package com.example.kafkapattern.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public void updateStatus(String eventId, OutboxEventStatus status) {
        OutboxEvent event = outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Outbox 이벤트를 찾을 수 없습니다: " + eventId));
        event.changeStatus(status);
    }
}
