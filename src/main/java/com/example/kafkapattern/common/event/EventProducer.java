package com.example.kafkapattern.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message);
    }
}
