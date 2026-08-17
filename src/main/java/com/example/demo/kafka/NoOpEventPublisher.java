package com.example.demo.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
    name = "kafka.enabled",
    havingValue = "false",
    matchIfMissing = true
)
public class NoOpEventPublisher implements EventPublisher {

    @Override
    public void publish(String topic, Object event) {
        // Kafka disabled — intentionally do nothing
    }
}