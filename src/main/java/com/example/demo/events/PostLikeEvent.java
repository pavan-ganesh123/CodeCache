package com.example.demo.events;

import java.time.Instant;

public record PostLikeEvent(Long postId,
    Long postOwnerId,
    Long likedByUserId,
    Instant likedAt) 
    {}
