package com.example.demo.services;

import com.example.demo.model.Message;
import com.example.demo.model.Post;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository repository;

    @Autowired
    private PostRepository postRepository;
    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    // =====================================================
    // SAVE MESSAGE
    // =====================================================

    public Message saveMessage(
            String messageId,
            Long senderId,
            Long receiverId,
            String content,
            String replyToMessageId
    ) {

        // ============================================
        // Prevent duplicate message insertion
        // ============================================

        Message existingMessage = repository
                .findByMessageId(messageId)
                .orElse(null);

        if (existingMessage != null) {
            return existingMessage;
        }

        Message message = new Message();

        message.setMessageId(messageId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);

        // reply feature
        message.setReplyToMessageId(replyToMessageId);

        // initially not starred
        message.setStarred(false);

        // expires after 1 hour
        message.setExpiresAt(
                Instant.now().plus(Duration.ofHours(1))
        );

        // delete flags
        message.setDeletedBySender(false);
        message.setDeletedByReceiver(false);

        // timestamps
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        return repository.save(message);
    }

    // =====================================================
    // GET CONVERSATION
    // =====================================================

    public List<Message> getMessages(
            Long senderId,
            Long receiverId
    ) {

        return repository.getConversation(
                senderId,
                receiverId
        );
    }

    // =====================================================
    // STAR MESSAGE
    // =====================================================

    public Message starMessage(String messageId) {

        Message message = repository.findByMessageId(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found")
                );

        // make permanent
        message.setStarred(true);

        // permanent messages never expire
        message.setExpiresAt(null);

        message.setUpdatedAt(LocalDateTime.now());

        return repository.save(message);
    }

    // =====================================================
    // UNSTAR MESSAGE
    // =====================================================

    public Message unstarMessage(String messageId) {

        Message message = repository.findByMessageId(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found")
                );

        // remove permanent status
        message.setStarred(false);

        // expires again after 1 hour
        message.setExpiresAt(
                Instant.now().plus(Duration.ofHours(1))
        );

        message.setUpdatedAt(LocalDateTime.now());

        return repository.save(message);
    }

    // =====================================================
    // DELETE FOR SENDER
    // =====================================================

    public Message deleteForSender(String messageId) {

        Message message = repository.findByMessageId(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found")
                );

        message.setDeletedBySender(true);

        message.setUpdatedAt(LocalDateTime.now());

        return repository.save(message);
    }

    // =====================================================
    // DELETE FOR RECEIVER
    // =====================================================

    public Message deleteForReceiver(String messageId) {

        Message message = repository.findByMessageId(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found")
                );

        message.setDeletedByReceiver(true);

        message.setUpdatedAt(LocalDateTime.now());

        return repository.save(message);
    }

    // =====================================================
    // CLEANUP EXPIRED MESSAGES
    // =====================================================

    @Scheduled(fixedRate = 300000)
    public void cleanupExpiredMessages() {

        repository.deleteExpiredMessages();

        System.out.println(
                "Expired messages cleaned at: "
                        + LocalDateTime.now()
        );
    }

    public Message deleteForEveryone(
        String messageId
        ) {

        Message message =
                repository.findByMessageId(messageId)
                .orElseThrow(
                        () -> new RuntimeException("Not found")
                );

        message.setDeletedForEveryone(true);

        message.setUpdatedAt(
                LocalDateTime.now()
        );

        return repository.save(message);
        }

        public Message saveSharedPost(
                String messageId,
                Long senderId,
                Long receiverId,
                Long sharedPostId,
                String content
        ) {
                Post post = postRepository.findById(sharedPostId)
                        .orElseThrow(() -> new RuntimeException("Post not found: " + sharedPostId));

                Message message = new Message();
                message.setMessageId(messageId);
                message.setSenderId(senderId);
                message.setReceiverId(receiverId);
                message.setContent(content);
                message.setMessageType("POST_SHARE");
                message.setSharedPost(post);

                return repository.save(message);
        }
}