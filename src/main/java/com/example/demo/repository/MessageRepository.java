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
        WHERE
        (
            (m.senderId = :senderId AND m.receiverId = :receiverId)
            OR
            (m.senderId = :receiverId AND m.receiverId = :senderId)
        )
        AND
        (
            m.expiresAt IS NULL
            OR m.expiresAt > CURRENT_TIMESTAMP
        )
        ORDER BY m.createdAt ASC
    """)
    List<Message> getConversation(@Param("senderId") Long senderId,@Param("receiverId") Long receiverId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Message m
        WHERE m.starred = false
        AND m.expiresAt < CURRENT_TIMESTAMP
    """)
    void deleteExpiredMessages();
}