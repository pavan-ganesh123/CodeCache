package com.example.demo.events;

import java.time.Instant;

public record PostShareEvent(
    Long senderId,
    Long receiverId,
    String messageId,
    Instant sentAt) {}
