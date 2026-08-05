package com.example.demo.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.events.CommentEvent;
import com.example.demo.events.FriendAcceptedEvent;
import com.example.demo.events.FriendRequestEvent;
import com.example.demo.events.MessageEvent;
import com.example.demo.events.PostLikeEvent;
import com.example.demo.services.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopics.POST_LIKE)
    public void consume(PostLikeEvent event) {
        System.out.println("Received PostLikeEvent : " + event);
        notificationService.create(event);
    }

    @KafkaListener(topics = KafkaTopics.COMMENT)
    public void consume(CommentEvent event) {
        System.out.println("Received CommentEvent : " + event);
        notificationService.create(event);
    }

    @KafkaListener(topics = KafkaTopics.FRIEND_REQUEST)
    public void consume(FriendRequestEvent event) {
        System.out.println("Received FriendRequestEvent : " + event);
        notificationService.create(event);
    }

    @KafkaListener(topics = KafkaTopics.FRIEND_ACCEPTED)
    public void consume(FriendAcceptedEvent event) {
        System.out.println("Received FriendAcceptedEvent : " + event);
        notificationService.create(event);
    }

    @KafkaListener(topics = KafkaTopics.MESSAGE)
    public void consume(MessageEvent event) {
        System.out.println("Received MessageEvent : " + event);
        notificationService.create(event);
    }
}