package com.example.demo.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "kafka.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class KafkaProducerService implements EventPublisher {

    private final KafkaTemplate<String, Object> kt;

    @Override
    public void publish(String topic, Object event) {
        kt.send(topic, event);
    }
}