package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

import jakarta.transaction.Transactional;
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

    @Transactional
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

        postRepository.incrementLikesCount(postId);
        postLikeRepository.save(like);
    }

    @Transactional
    public void unlikePost(Long postId) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return;
        }

        Optional<PostLike> likeOpt = postLikeRepository
                .findByPostIdAndUserId(postId, userId);

        if (likeOpt.isPresent()) {
            postLikeRepository.delete(likeOpt.get());
            postRepository.decrementLikesCount(postId);
        }
    }

    @Transactional
    public PostComment addComment(
            Long postId,
            String username,
            String text,
            Long userId) {

        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setUsername(username);
        comment.setComment(text);
        comment.setCreatedAt(LocalDateTime.now());

        PostComment saved = postCommentRepository.save(comment);

        // Increment comments count on the post
        postRepository.incrementCommentsCount(postId);

        return saved;
    }

    public List<PostComment> getComments(
        Long postId) {

        return postCommentRepository
                .findByPostIdOrderByCreatedAtDesc(postId);
    }

    public List<Post> getFeed() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getPostsByUser(Long userId) {

        return postRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Post getPost(Long postId) {
        return postRepository
                .findById(postId)
                .orElseThrow(
                    () -> new RuntimeException("Post not found")
                );
    }
}