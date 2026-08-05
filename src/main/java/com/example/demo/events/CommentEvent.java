package com.example.demo.events;

import java.time.Instant;

public record CommentEvent(
    Long postId,
    Long postOwnerId,
    Long commentedByUserId,
    String commentText,
    Instant commentedAt) {}
