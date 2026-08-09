package com.example.demo.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.events.MessageEvent;
import com.example.demo.services.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopics.MESSAGE, groupId = "notification-group")
    public void consume(MessageEvent event) {
        System.out.println("Received MessageEvent : " + event);
        notificationService.create(event);
    }
}