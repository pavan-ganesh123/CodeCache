package com.example.demo.services;

import com.example.demo.dto.PostImageDTO;
import com.example.demo.model.Post;
import com.example.demo.model.PostImage;
import com.example.demo.repository.PostImageRepository;
import com.example.demo.repository.PostRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostImageService {

    @Autowired
    private PostImageRepository postImageRepo;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CacheManager cacheManager;

    // A post's images feed both myPosts (FeedPostDTO.imageUrl, the
    // primary image) and postDetail (PostDetailDTO.imageUrls, the
    // published list). Any image mutation could affect either one, so
    // both get evicted rather than trying to track precisely which
    // field each specific method call actually changes.
    private void evictPostCaches(Long ownerUserId, Long postId) {
        Cache myPosts = cacheManager.getCache("myPosts");
        if (myPosts != null) myPosts.evict(ownerUserId);

        Cache postDetail = cacheManager.getCache("postDetail");
        if (postDetail != null) postDetail.evict(postId);
    }

    public PostImage seedImage(Long postId, String imageUrl, Long adminUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        PostImage img = new PostImage();
        img.setPost(post);
        img.setImageUrl(imageUrl);
        img.setUploadedBy(adminUserId);
        img.setCaption(null);
        img.setIsPrimary(true);
        img.setStatus("PUBLISHED");

        PostImage saved = postImageRepo.save(img);

        // Evict under the post's actual owner, not adminUserId — the
        // cache key for myPosts is whoever's feed/post-list this image
        // shows up under, which is post.getUserId(), not the admin who
        // seeded it.
        evictPostCaches(post.getUserId(), postId);

        return saved;
    }

    public PostImageDTO uploadImage(Long postId, String imageUrl, Long userId, boolean requireModeration) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to upload image for this post");
        }
        PostImage img = new PostImage();
        img.setPost(post);
        img.setImageUrl(imageUrl);
        img.setUploadedBy(userId);
        img.setCaption(null);
        img.setIsPrimary(false);
        img.setStatus(requireModeration ? "PENDING" : "PUBLISHED");
        PostImage saved = postImageRepo.save(img);

        evictPostCaches(userId, postId);

        return toDto(saved,post);
    }
    private PostImageDTO toDto(PostImage img, Post post) {
        PostImageDTO dto = new PostImageDTO();
        dto.setId(img.getId());
        dto.setImageUrl(img.getImageUrl());
        dto.setCaption(img.getCaption());
        dto.setIsPrimary(img.getIsPrimary());
        dto.setStatus(img.getStatus());
        dto.setUploadedBy(img.getUploadedBy());
        dto.setCreatedAt(img.getCreatedAt());
        dto.setPostId(post.getId());
        dto.setPostQuestionTitle(post.getQuestionTitle());
        dto.setPostUserName(post.getUserName());
        return dto;
    }
    public List<PostImage> getPrimaryImages(List<Long> postIds) {
        return postImageRepo.findByPostIdInAndIsPrimaryTrue(postIds);
    }

    public void setPrimaryImage(Long postId, Long imageId) {
        PostImage currentPrimary = postImageRepo.findFirstByPostIdAndIsPrimaryTrue(postId).orElse(null);
        if (currentPrimary != null) {
            currentPrimary.setIsPrimary(false);
            postImageRepo.save(currentPrimary);
        }

        PostImage img = postImageRepo.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        if (!img.getPost().getId().equals(postId)) {
            throw new RuntimeException("Image does not belong to this post");
        }

        img.setIsPrimary(true);
        postImageRepo.save(img);

        // No userId parameter here — pull the owner off the image's
        // post instead, same as seedImage does.
        evictPostCaches(img.getPost().getUserId(), postId);
    }

    public List<PostImage> getPublishedImages(Long postId) {
        return postImageRepo.findByPostIdAndStatus(postId, "PUBLISHED");
    }

    public PostImage getPrimaryImage(Long postId) {
        return postImageRepo.findFirstByPostIdAndIsPrimaryTrue(postId).orElse(null);
    }

    @Transactional
    public PostImageDTO updatePostImage(Long postId, String imageUrl, Long userId, boolean requireModeration) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to upload image for this post");
        }

        PostImage img = postImageRepo.findByPostId(postId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No existing image found for post: " + postId));

        img.setPost(post);
        img.setImageUrl(imageUrl);
        img.setUploadedBy(userId);
        img.setCaption(null);
        img.setIsPrimary(true);
        img.setStatus(requireModeration ? "PENDING" : "PUBLISHED");

        PostImage saved = postImageRepo.save(img);

        evictPostCaches(userId, postId);

        return toDto(saved, post); // pass the fully-loaded post explicitly, don't rely on saved.getPost()
    }
}