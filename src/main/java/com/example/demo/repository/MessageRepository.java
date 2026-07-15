package com.example.demo.repository;

import com.example.demo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByMessageId(String messageId);

    @Query("""
    SELECT m
    FROM Message m
    LEFT JOIN FETCH m.sharedPost sp
    WHERE
    (
        (m.senderId = :currentUserId AND m.receiverId = :otherUserId)
        OR
        (m.senderId = :otherUserId AND m.receiverId = :currentUserId)
    )
    AND
    (
        m.expiresAt IS NULL
        OR m.expiresAt > CURRENT_TIMESTAMP
    )
    AND
    (
        (m.senderId = :currentUserId AND m.deletedBySender = false)
        OR
        (m.receiverId = :currentUserId AND m.deletedByReceiver = false)
    )
    ORDER BY m.createdAt
    """)
    List<Message> getConversation(
        @Param("currentUserId") Long currentUserId,
        @Param("otherUserId") Long otherUserId
    );
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Message m
        WHERE m.starred = false
        AND m.expiresAt < CURRENT_TIMESTAMP
    """)
    void deleteExpiredMessages();
}