package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Friend;
import com.example.demo.model.enums.FriendStatus;

public interface FriendRepository extends JpaRepository<Friend, Long>{
    Optional<Friend> findByUserIdAndFriendId(Long userId, Long friendId);

    Optional<Friend> findByUserIdAndFriendIdOrUserIdAndFriendId(
        Long user1, Long friend1,
        Long user2, Long friend2
    );

    List<Friend> findByUserIdAndStatus(Long userId, FriendStatus status);

    List<Friend> findByFriendIdAndStatus(Long friendId, FriendStatus status);

    @Query("SELECT f FROM Friend f WHERE f.user.id = :userId OR f.friend.id = :userId")
    List<Friend> findAllRelations(Long userId);

    @Query("""
    SELECT f
    FROM Friend f
    WHERE
    (f.user.id = :userId AND f.friend.id = :targetUserId)
    OR
    (f.user.id = :targetUserId AND f.friend.id = :userId)
    """)
    Optional<Friend> findRelation(
        @Param("userId") Long userId,
        @Param("targetUserId") Long targetUserId
    );
}
