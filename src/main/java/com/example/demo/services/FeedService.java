package com.example.demo.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    // Left uncached deliberately — see the note further down. Caching
    // this correctly would need page+size+seed in the key (not just
    // currentUserId, which would return page 1's results for every
    // page requested). Since `seed` is regenerated per browser session,
    // a correctly-scoped cache mostly wouldn't hit anyway. Fixing the
    // N+1 in toDto (below) matters far more than caching this method.
    public Page<FeedPostDTO> getFeed(
            Long currentUserId,
            int page,
            int size,
            Long seed
    ) {

        List<Long> friendIds =
                friendService.getFriendIds(currentUserId);

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Post> posts = postRepository.getFeed(
                currentUserId,
                friendIds,
                PostVisibility.PUBLIC,
                PostVisibility.FRIENDS,
                seed,
                pageable
        );

        // .map(this::toDto) replaced with a batched version — the old
        // one ran two extra queries (primary image + user lookup) per
        // post inside the loop. This fetches both in one query each for
        // the whole page instead of one query each per post.
        List<FeedPostDTO> dtos = toDtoBatch(posts.getContent());
        return new PageImpl<>(dtos, pageable, posts.getTotalElements());
    }

    // Replace postRepository.****(userId) with your actual repository
    // method name — this line won't compile as-is.
    @Cacheable(value = "myPosts", key = "#userId")
    public List<FeedPostDTO> getPostsByUser(Long userId) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId); // ← your real method name goes here
        return toDtoBatch(posts);
    }

    // Batched replacement for the old per-post toDto loop. Two queries
    // total for the whole list, instead of two queries per post.
    private List<FeedPostDTO> toDtoBatch(List<Post> posts) {
        if (posts.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = posts.stream().map(Post::getId).toList();
        List<Long> userIds = posts.stream().map(Post::getUserId).distinct().toList();

        Map<Long, PostImage> primaryByPostId = postImageService.getPrimaryImages(postIds)
                .stream()
                .collect(Collectors.toMap(img -> img.getPost().getId(), img -> img));

        Map<Long, User> usersById = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return posts.stream().map(post -> toDto(post, primaryByPostId.get(post.getId()), usersById.get(post.getUserId())))
                .toList();
    }

    private FeedPostDTO toDto(Post post, PostImage primary, User postUser) {
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

        if (primary != null) {
            dto.setImageUrl(primary.getImageUrl());
        }

        // postUser can be null only if the user was deleted after the
        // post was created — falling back to no picture rather than
        // throwing, since a missing avatar shouldn't break the whole feed.
        dto.setProfilePicture(postUser != null ? postUser.getProfilePicture() : null);

        return dto;
    }
}