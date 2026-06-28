package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dto.FeedPostDTO;
import com.example.demo.model.Post;
import com.example.demo.model.PostImage;
import com.example.demo.model.User;
import com.example.demo.model.enums.PostVisibility;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;

@Service
public class FeedService {

    private final PostRepository postRepository;
    private final FriendService friendService;
    private final PostImageService postImageService;
    private final UserRepository userRepository;

    public FeedService(
            PostRepository postRepository,
            FriendService friendService, 
            PostImageService postImageService, UserRepository userRepository) {

        this.postRepository = postRepository;
        this.friendService = friendService;
        this.postImageService = postImageService;
        this.userRepository = userRepository;
    }

    public Page<FeedPostDTO> getFeed(
            Long currentUserId,
            int page,
            int size
    ) {

        List<Long> friendIds =
                friendService.getFriendIds(currentUserId);

        Pageable pageable =
                PageRequest.of(page, size);

        return postRepository.getFeed(
                currentUserId,
                friendIds,
                PostVisibility.PUBLIC,
                PostVisibility.FRIENDS,
                pageable
        ).map(this::toDto);
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
        User postUser = userRepository.findById(post.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        
        dto.setProfilePicture(postUser.getProfilePicture());
        return dto;
    }
}
