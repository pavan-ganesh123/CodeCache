package com.example.demo.controllers;

import com.example.demo.model.Message;
import com.example.demo.services.MessageService;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MessageResolver {

    private final MessageService service;

    public MessageResolver(MessageService service) {
        this.service = service;
    }

    // =========================
    // Query
    // =========================

    @QueryMapping
    public List<Message> messages(
            @Argument Long senderId,
            @Argument Long receiverId
    ) {
        return service.getMessages(senderId, receiverId);
    }

    // =========================
    // Save Message
    // =========================

    @MutationMapping
    public Message saveMessage(
            @Argument String messageId,
            @Argument Long senderId,
            @Argument Long receiverId,
            @Argument String content,
            @Argument String replyToMessageId
    ) {

        return service.saveMessage(
                messageId,
                senderId,
                receiverId,
                content,
                replyToMessageId
        );
    }

    // =========================
    // Star Message
    // =========================

    @MutationMapping
    public Message starMessage(
            @Argument String messageId
    ) {
        return service.starMessage(messageId);
    }

    // =========================
    // Unstar Message
    // =========================

    @MutationMapping
    public Message unstarMessage(
            @Argument String messageId
    ) {
        return service.unstarMessage(messageId);
    }
    @MutationMapping
    public Message deleteForEveryone(
            @Argument String messageId
    ) {

        return service
                .deleteForEveryone(messageId);
    }
}