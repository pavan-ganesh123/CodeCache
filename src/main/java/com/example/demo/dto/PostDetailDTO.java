package com.example.demo.dto;

import java.util.List;

import com.example.demo.model.Post;
import com.example.demo.model.PostImage;

public record PostDetailDTO(
        Long id,
        Long userId,
        String userName,
        Long questionId,
        String questionTitle,
        String difficulty,
        Integer likesCount,
        Integer commentsCount,
        List<String> imageUrls
) {
    public static PostDetailDTO from(Post post) {
        return new PostDetailDTO(
                post.getId(),
                post.getUserId(),
                post.getUserName(),
                post.getQuestionId(),
                post.getQuestionTitle(),
                post.getDifficulty(),
                post.getLikesCount(),
                post.getCommentsCount(),
                post.getImages().stream()
                        .filter(img -> "PUBLISHED".equals(img.getStatus()))
                        .map(PostImage::getImageUrl)
                        .toList()
        );
    }
}