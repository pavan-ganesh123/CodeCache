package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Post;
import com.example.demo.model.PostComment;
import com.example.demo.model.PostLike;
import com.example.demo.model.Problem;
import com.example.demo.repository.PostCommentRepository;
import com.example.demo.repository.PostLikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private SecurityUtil securityUtil;

    public Post createProblemPost(
            Long userId,
            String username,
            Problem problem) {

        Post post = new Post();

        post.setUserId(userId);
        post.setUserName(username);
        post.setQuestionId(problem.getId());
        post.setQuestionTitle(problem.getQuestionName());
        post.setDifficulty(problem.getDifficulty());
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public void likePost(Long postId) {

        Long userId = securityUtil.getCurrentUserId();

        if(userId == null)
            return;

        boolean alreadyLiked =
                postLikeRepository
                .findByPostIdAndUserId(postId, userId)
                .isPresent();

        if(alreadyLiked)
            return;

        PostLike like = new PostLike();

        like.setPostId(postId);
        like.setUserId(userId);
        like.setLikedAt(LocalDateTime.now());

        postLikeRepository.save(like);
    }

    public void unlikePost(Long postId) {

        Long userId = securityUtil.getCurrentUserId();

        postLikeRepository
                .findByPostIdAndUserId(postId, userId)
                .ifPresent(postLikeRepository::delete);
    }

    public PostComment addComment(
            Long postId,
            String username,
            String text,
            Long userId) {

        PostComment comment =
                new PostComment();

        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setUsername(username);
        comment.setComment(text);
        comment.setCreatedAt(LocalDateTime.now());

        return postCommentRepository.save(comment);
    }

    public List<PostComment> getComments(
        Long postId) {

        return postCommentRepository
                .findByPostIdOrderByCreatedAtDesc(postId);
    }

    public List<Post> getFeed() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }
}