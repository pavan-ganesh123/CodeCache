package com.example.demo.kafka;

public interface EventPublisher {
    void publish(String topic, Object event);
}
