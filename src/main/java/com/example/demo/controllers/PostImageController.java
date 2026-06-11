package com.example.demo.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import com.example.demo.dto.PostImageDTO;
import com.example.demo.model.PostImage;
import com.example.demo.security.SecurityUtil;
import com.example.demo.services.PostImageService;
import com.example.demo.services.UploadService;

@RestController
@RequestMapping("/api/posts")
public class PostImageController {

    @Autowired
    private PostImageService imageService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private SecurityUtil securityUtil;

    // Admin seeds an image (URL from external source)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{postId}/images/seed")
    public PostImage seedImage(
        @PathVariable Long postId,
        @RequestParam String imageUrl,
        @RequestParam Long adminUserId) {
        return imageService.seedImage(postId, imageUrl, adminUserId);
    }

    // User uploads an image (multipart file)
    @PostMapping("/{postId}/images")
    public PostImageDTO uploadImage(
        @PathVariable Long postId,
        @RequestParam MultipartFile file) {
        Long userId = securityUtil.getCurrentUserId();
        try {
            String imageUrl = uploadService.uploadImage(file);
            return imageService.uploadImage(postId, imageUrl, userId, true); // require moderation
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
        }
    }

    // Set primary image
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{postId}/images/{imageId}/primary")
    public PostImage setPrimary(@PathVariable Long postId, @PathVariable Long imageId) {
        imageService.setPrimaryImage(postId, imageId);
        return imageService.getPrimaryImage(postId);
    }
}