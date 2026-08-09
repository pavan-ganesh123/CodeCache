package com.example.demo.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.FeedPostDTO;
import com.example.demo.dto.PostDetailDTO;
import com.example.demo.events.PostLikeEvent;
import com.example.demo.events.CommentEvent;
import com.example.demo.kafka.KafkaProducerService;
import com.example.demo.kafka.KafkaTopics;
import com.example.demo.model.Post;
import com.example.demo.model.PostComment;
import com.example.demo.model.PostLike;
import com.example.demo.model.Problem;
import com.example.demo.model.enums.PostVisibility;
import com.example.demo.repository.PostCommentRepository;
import com.example.demo.repository.PostLikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserProblemRepository;
import com.example.demo.security.SecurityUtil;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final KafkaProducerService producer;

    @Autowired
    private PostLikeRepository postLikeRepository;


    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private FriendService friendService;    

    @Autowired
    private UserProblemRepository upRepo;

    @Autowired
    private CacheManager cacheManager;

    // Post detail's likesCount/commentsCount change on every like and
    // every comment — evict rather than lean on the 30s TTL alone.
    private void evictPostDetailCache(Long postId) {
        Cache postDetail = cacheManager.getCache("postDetail");
        if (postDetail != null) postDetail.evict(postId);
    }

    // Without this, a freshly created post doesn't show up on the
    // user's own "My Posts" page until the 10-minute myPosts TTL
    // expires — the single most noticeable staleness bug of the bunch.
    private void evictMyPostsCache(Long userId) {
        Cache myPosts = cacheManager.getCache("myPosts");
        if (myPosts != null) myPosts.evict(userId);
    }

    public Post createProblemPost(
            Long userId,
            String username,
            Problem problem,
            PostVisibility visibility) {

        if (upRepo.existsByUserIdAndProblemId(userId, problem.getId())) {
            throw new RuntimeException("Problem already solved by this user");
        }
        Post post = new Post();

        post.setUserId(userId);
        post.setUserName(username);
        post.setQuestionId(problem.getId());
        post.setQuestionTitle(problem.getQuestionName());
        post.setDifficulty(problem.getDifficulty());
        post.setCreatedAt(LocalDateTime.now());
        post.setVisibility(visibility);

        Post saved = postRepository.save(post);

        evictMyPostsCache(userId);

        // Note: if you ever add caching to FeedService.getFeed, this is
        // also where you'd need to evict every friend's feed cache —
        // a new post changes what they'd see. Not needed today since
        // getFeed is currently uncached.

        return saved;
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

        evictPostDetailCache(postId);

        Post likedPost = postRepository.getReferenceById(postId);
        try {
            System.out.println("Publishing...");

            producer.publish(
                KafkaTopics.POST_ACTIVITY,  
                new PostLikeEvent(postId, likedPost.getUserId(), userId, Instant.now())
            );

            System.out.println("Published!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
            evictPostDetailCache(postId);
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

        evictPostDetailCache(postId);

        Post commentedPost=postRepository.getReferenceById(postId);
        try {
            System.out.println("Publishing Comment...");

            producer.publish(KafkaTopics.POST_ACTIVITY, new CommentEvent(postId, commentedPost.getUserId(),userId, text, Instant.now()));
            System.out.println("Comment Published!!!");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return saved;
    }

    public List<PostComment> getComments(
        Long postId) {

        return postCommentRepository
                .findByPostIdOrderByCreatedAtDesc(postId);
    }

    
    @Cacheable(value = "postDetail", key = "#postId")
    @Transactional(readOnly = true)
    public PostDetailDTO getPost(Long postId) {
        Post post = postRepository
                .findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    
        return PostDetailDTO.from(post);
    }
}