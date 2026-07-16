package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Friend;
import com.example.demo.model.enums.FriendStatus;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    
    // Existing methods (kept as-is)
    Optional<Friend> findByUserIdAndFriendId(Long userId, Long friendId);

    Optional<Friend> findByUserIdAndFriendIdOrUserIdAndFriendId(
        Long user1, Long friend1,
        Long user2, Long friend2
    );

    List<Friend> findByUserIdAndStatus(Long userId, FriendStatus status);

    @Query("SELECT f FROM Friend f JOIN FETCH f.user JOIN FETCH f.friend WHERE f.friend.id = :friendId AND f.status = :status")
    List<Friend> findByFriendIdAndStatus(@Param("friendId") Long friendId, @Param("status") FriendStatus status);

    @Query("""
    SELECT f
    FROM Friend f
    JOIN FETCH f.user
    JOIN FETCH f.friend
    WHERE f.user.id = :userId
    """)
    List<Friend> findAllRelations(Long userId);

    @Query("""
        SELECT f
        FROM Friend f
        WHERE
        f.user.id = :userId AND f.friend.id = :targetUserId
        """)
    Optional<Friend> findRelation(
        @Param("userId") Long userId,
        @Param("targetUserId") Long targetUserId
    );
    
    // New method: Get all accepted friend IDs for a user (bidirectional)
    @Query("""
        SELECT CASE WHEN f.user.id = :userId THEN f.friend.id ELSE f.user.id END
        FROM Friend f
        WHERE (f.user.id = :userId)
        AND f.status = :status
        """)
    List<Long> getAcceptedFriendIds(
        @Param("userId") Long userId,
        @Param("status") FriendStatus status
    );
    
    // New method: Find all accepted friend relations for a user (bidirectional)
    @Query("""
        SELECT f FROM Friend f
        WHERE (f.user.id = :userId OR f.friend.id = :userId)
        AND f.status = :status
        """)
    List<Friend> findAllPendingFriends(
        @Param("userId") Long userId,
        @Param("status") FriendStatus status
    );

    @Query("SELECT DISTINCT " +
           "CASE WHEN f.user.id = :userId THEN f.friend.id " +
           "ELSE f.user.id END " +
           "FROM Friend f " +
           "WHERE (f.user.id = :userId) " +
           "AND (f.status = 'ACCEPTED' OR f.status = 'PENDING')")
    Set<Long> getExcludedFriendIds(@Param("userId") Long userId);
}