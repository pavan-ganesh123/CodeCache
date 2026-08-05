package com.example.demo.events;

import java.time.Instant;

public record FriendRequestEvent(Long senderId,
    Long receiverId,
    Instant sentAt) {}
