package com.example.demo.kafka;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.events.FriendAcceptedEvent;
import com.example.demo.events.FriendRequestEvent;
import com.example.demo.services.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.FRIEND_ACTIVITY, groupId = "notification-group")
public class FriendActivityListener {

    private final NotificationService notificationService;

    @KafkaHandler
    public void consume(FriendRequestEvent event) {
        System.out.println("Received FriendRequestEvent : " + event);
        notificationService.create(event);
    }

    @KafkaHandler
    public void consume(FriendAcceptedEvent event) {
        System.out.println("Received FriendAcceptedEvent : " + event);
        notificationService.create(event);
    }

    @KafkaHandler(isDefault = true)
    public void consumeUnknown(Object event) {
        System.out.println("Unhandled friend-activity event type: " + event);
    }
}