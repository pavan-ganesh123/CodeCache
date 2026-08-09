package com.example.demo.dto;

public record BlockingUserDTO(
        Long id,
        String userName,
        String email
) {
    public static BlockingUserDTO from(com.example.demo.model.User user) {
        return new BlockingUserDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail()
        );
    }
}