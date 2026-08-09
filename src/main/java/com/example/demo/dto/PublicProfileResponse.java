package com.example.demo.dto;

public record PublicProfileResponse(
        Long id,
        String userName,
        String profilePicture
) {}