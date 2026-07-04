package com.example.demo.repository;

import com.example.demo.model.Post;
import com.example.demo.model.enums.PostVisibility;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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


    @Query(
    value = """
        SELECT *
        FROM post p
        WHERE
            p.visibility = :publicVisibility
            OR (
                p.visibility = :friendsVisibility
                AND p.user_id IN (:friendIds)
            )
            OR p.user_id = :currentUserId
        ORDER BY MD5(CONCAT(CAST(p.id AS TEXT), CAST(:seed AS TEXT)))
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM post p
            WHERE
                p.visibility = :publicVisibility
                OR (
                    p.visibility = :friendsVisibility
                    AND p.user_id IN (:friendIds)
                )
                OR p.user_id = :currentUserId
            """,
        nativeQuery = true
    )
    Page<Post> getFeedInternal(
            @Param("currentUserId") Long currentUserId,
            @Param("friendIds") List<Long> friendIds,
            @Param("publicVisibility") String publicVisibility,
            @Param("friendsVisibility") String friendsVisibility,
            @Param("seed") Long seed,
            Pageable pageable
    );
    default Page<Post> getFeed(
        Long currentUserId,
        List<Long> friendIds,
        PostVisibility publicVisibility,
        PostVisibility friendsVisibility,
        Long seed,
        Pageable pageable
    ) {
        return getFeedInternal(
                currentUserId,
                friendIds,
                publicVisibility.name(),
                friendsVisibility.name(),
                seed,
                pageable
        );
    }
}
