package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.FeedPostDTO;
import com.example.demo.model.Post;
import com.example.demo.model.PostImage;
import com.example.demo.model.enums.PostVisibility;
import com.example.demo.repository.PostRepository;

@Service
public class FeedService {

    private final PostRepository postRepository;
    private final FriendService friendService;
    private final PostImageService postImageService;

    public FeedService(
            PostRepository postRepository,
            FriendService friendService, 
            PostImageService postImageService) {

        this.postRepository = postRepository;
        this.friendService = friendService;
        this.postImageService = postImageService;
    }

    public List<FeedPostDTO> getFeed(Long currentUserId) {
        List<Long> friendIds = friendService.getFriendIds(currentUserId);

        List<Post> posts = postRepository.getFeed(
                currentUserId,
                friendIds,
                PostVisibility.PUBLIC,
                PostVisibility.FRIENDS
        );

        return posts.stream()
                .map(this::toDto)
                .toList();
    }
    private FeedPostDTO toDto(Post post) {
        FeedPostDTO dto = new FeedPostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setUserName(post.getUserName());
        dto.setQuestionId(post.getQuestionId());
        dto.setQuestionTitle(post.getQuestionTitle());
        dto.setDifficulty(post.getDifficulty());
        dto.setLikesCount(post.getLikesCount());
        dto.setCommentsCount(post.getCommentsCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setSolvedAt(post.getSolvedAt());
        dto.setVisibility(post.getVisibility());

        PostImage primary = postImageService.getPrimaryImage(post.getId());
        if (primary != null) {
            dto.setImageUrl(primary.getImageUrl());
        }

        return dto;
    }
}
