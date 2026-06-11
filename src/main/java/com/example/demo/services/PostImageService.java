package com.example.demo.services;

import com.example.demo.dto.PostImageDTO;
import com.example.demo.model.Post;
import com.example.demo.model.PostImage;
import com.example.demo.repository.PostImageRepository;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostImageService {

    @Autowired
    private PostImageRepository postImageRepo;

    @Autowired
    private PostRepository postRepository;

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

        return postImageRepo.save(img);
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
        return toDto(saved);
    }
    private PostImageDTO toDto(PostImage img) {
        PostImageDTO dto = new PostImageDTO();
        dto.setId(img.getId());
        dto.setImageUrl(img.getImageUrl());
        dto.setCaption(img.getCaption());
        dto.setIsPrimary(img.getIsPrimary());
        dto.setStatus(img.getStatus());
        dto.setUploadedBy(img.getUploadedBy());
        dto.setCreatedAt(img.getCreatedAt());

        if (img.getPost() != null) {
            dto.setPostId(img.getPost().getId());
            dto.setPostQuestionTitle(img.getPost().getQuestionTitle());
            dto.setPostUserName(img.getPost().getUserName());
        }

        return dto;
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
    }

    public List<PostImage> getPublishedImages(Long postId) {
        return postImageRepo.findByPostIdAndStatus(postId, "PUBLISHED");
    }

    public PostImage getPrimaryImage(Long postId) {
        return postImageRepo.findFirstByPostIdAndIsPrimaryTrue(postId).orElse(null);
    }

}