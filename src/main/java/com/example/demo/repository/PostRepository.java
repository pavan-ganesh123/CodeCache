package com.example.demo.repository;

import com.example.demo.model.Post;
import com.example.demo.model.enums.PostVisibility;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Post> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);
    
    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount + 1 WHERE p.id = :postId")
    void incrementLikesCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount - 1 WHERE p.id = :postId")
    void decrementLikesCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount + 1 WHERE p.id = :postId")
    void incrementCommentsCount(@Param("postId") Long postId);


   @Query("""
    SELECT p
    FROM Post p
    WHERE
        p.visibility = :publicVisibility
        OR (
            p.visibility = :friendsVisibility
            AND p.userId IN :friendIds
        )
        OR p.userId = :currentUserId
    ORDER BY p.createdAt DESC
    """)
    List<Post> getFeed(
            Long currentUserId,
            List<Long> friendIds,
            PostVisibility publicVisibility,
            PostVisibility friendsVisibility);
}
