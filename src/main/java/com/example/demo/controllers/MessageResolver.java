package com.example.demo.controllers;

import com.example.demo.model.Message;
import com.example.demo.model.Post;
import com.example.demo.model.PostImage;
import com.example.demo.repository.PostImageRepository;
import com.example.demo.services.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MessageResolver {

    private final MessageService service;

    @Autowired
    private PostImageRepository postImageRepo;
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

        @MutationMapping
        public Message saveSharedPost(
                @Argument String messageId,
                @Argument Long senderId,
                @Argument Long receiverId,
                @Argument Long sharedPostId,
                @Argument String content
        ) {
        return service.saveSharedPost(
                messageId,
                senderId,
                receiverId,
                sharedPostId,
                content
        );
        }

        @SchemaMapping(typeName = "Post", field = "primaryImageUrl")
        public String primaryImageUrl(Post post) {

        return postImageRepo.findFirstByPostIdAndIsPrimaryTrue(post.getId())
                .map(PostImage::getImageUrl)
                .orElse(null);
        }
        
}