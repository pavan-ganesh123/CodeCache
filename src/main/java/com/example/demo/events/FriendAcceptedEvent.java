package com.example.demo.events;

import java.time.Instant;

public record FriendAcceptedEvent(
    Long accepterId,
    Long friendId,
    Instant acceptedAt
) {}