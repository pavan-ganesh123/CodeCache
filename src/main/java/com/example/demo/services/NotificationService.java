package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.events.CommentEvent;
import com.example.demo.events.FriendAcceptedEvent;
import com.example.demo.events.FriendRequestEvent;
import com.example.demo.events.MessageEvent;
import com.example.demo.events.NotificationType;
import com.example.demo.events.PostLikeEvent;
import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepo;

    public void create(PostLikeEvent event){
        Notification notification = Notification.builder()
                            .receiverId(event.postOwnerId())
                            .senderId(event.likedByUserId())
                            .type(NotificationType.POST_LIKE)
                            .referenceId(String.valueOf(event.postId()))
                            .read(false)
                            .createdAt(event.likedAt())
                            .build();
        notificationRepo.save(notification);
    }

    public void create(CommentEvent event){
        Notification notification = Notification.builder()
                        .receiverId(event.postOwnerId())
                        .senderId(event.commentedByUserId())
                        .type(NotificationType.POST_COMMENT)
                        .referenceId(String.valueOf(event.postId()))
                        .read(false)
                        .createdAt(event.commentedAt())
                        .build();
        notificationRepo.save(notification);
    }

    public void create(FriendRequestEvent event){
        Notification notification = Notification.builder()
            .receiverId(event.receiverId())
            .senderId(event.senderId())
            .type(NotificationType.FRIEND_REQUEST)
            .referenceId(String.valueOf(event.senderId()))
            .read(false)
            .createdAt(event.sentAt())
            .build();

        notificationRepo.save(notification);
    }

    public void create(FriendAcceptedEvent event) {

        Notification notification = Notification.builder()
                .receiverId(event.friendId())
                .senderId(event.accepterId())
                .type(NotificationType.FRIEND_ACCEPTED)
                .referenceId(String.valueOf(event.accepterId()))
                .read(false)
                .createdAt(event.acceptedAt())
                .build();

        notificationRepo.save(notification);
    }
    public void create(MessageEvent event) {

        Notification notification = Notification.builder()
                .receiverId(event.receiverId())
                .senderId(event.senderId())
                .type(NotificationType.MESSAGE)
                .referenceId(event.messageId())
                .read(false)
                .createdAt(event.sentAt())
                .build();

        notificationRepo.save(notification);
    }
}
