package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CommentRequest;
import com.example.demo.model.Post;
import com.example.demo.model.PostComment;
import com.example.demo.security.SecurityUtil;
import com.example.demo.services.PostService;

@RestController
@RequestMapping("/api/posts")
public class PageController {
    @Autowired
    private PostService postService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/{postId}/like")
    public void likePost(
            @PathVariable Long postId) {

        postService.likePost(postId);
    }

    @DeleteMapping("/{postId}/like")
    public void unlikePost(
            @PathVariable Long postId) {

        postService.unlikePost(postId);
    }

    @PostMapping("/{postId}/comments")
    public PostComment addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            Authentication authentication) {

        Long userId =
                securityUtil.getCurrentUserId();

        return postService.addComment(
                postId,
                authentication.getName(),
                request.getComment(),
                userId);
    }

    @GetMapping("/{postId}/comments")
    public List<PostComment> getComments(
            @PathVariable Long postId) {

        return postService.getComments(postId);
    }

    @GetMapping("/feed")
    public List<Post> getFeed() {
        return postService.getFeed();
    }

    @GetMapping("/mine")
    public List<Post> getMyPosts() {

        Long userId = securityUtil.getCurrentUserId();

        return postService.getPostsByUser(userId);
    }

    @GetMapping("/{postId}")
    public Post getPost(
            @PathVariable Long postId) {

        return postService.getPost(postId);
    }
}
