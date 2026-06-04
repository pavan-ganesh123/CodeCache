package com.example.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Post;
import com.example.demo.model.enums.PostVisibility;
import com.example.demo.repository.PostRepository;

@Service
public class FeedService {

    private final PostRepository postRepository;
    private final FriendService friendService;

    public FeedService(
            PostRepository postRepository,
            FriendService friendService) {

        this.postRepository = postRepository;
        this.friendService = friendService;
    }

    public List<Post> getFeed(Long currentUserId) {

        List<Long> friendIds =
                friendService.getFriendIds(currentUserId);

        return postRepository.getFeed(
                currentUserId,
                friendIds,
                PostVisibility.PUBLIC,
        PostVisibility.FRIENDS);
    }
}
