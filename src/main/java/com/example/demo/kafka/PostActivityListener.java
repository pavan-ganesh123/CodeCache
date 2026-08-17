package com.example.demo.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.events.CommentEvent;
import com.example.demo.events.PostLikeEvent;
import com.example.demo.events.PostShareEvent;
import com.example.demo.services.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "kafka.enabled",
    havingValue = "true",
    matchIfMissing = false
)
@KafkaListener(topics = KafkaTopics.POST_ACTIVITY, groupId = "notification-group")
public class PostActivityListener {

    private final NotificationService notificationService;

    @KafkaHandler
    public void consume(PostLikeEvent event) {
        System.out.println("Received PostLikeEvent : " + event);
        notificationService.create(event);
    }

    @KafkaHandler
    public void consume(CommentEvent event) {
        System.out.println("Received CommentEvent : " + event);
        notificationService.create(event);
    }

    @KafkaHandler
    public void consume(PostShareEvent event) {
        System.out.println("Received PostShareEvent : " + event);
        notificationService.create(event);
    }

    @KafkaHandler(isDefault = true)
    public void consumeUnknown(Object event) {
        System.out.println("Unhandled post-activity event type: " + event);
    }
}