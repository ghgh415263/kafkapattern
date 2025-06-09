package com.example.kafkapattern.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    List<OutboxEvent> findTop10ByStatusOrderByCreatedAtAsc(OutboxEventStatus outboxEventStatus);
}

