package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {
        Page<Notification> findByReceiverIdOrderByCreatedAtDesc(
            Long receiverId,
            Pageable pageable
    );
        List<Notification> findByReceiverIdAndReadFalse(Long receiverId);

    long countByReceiverIdAndReadFalse(Long receiverId);

    void deleteByReceiverId(Long receiverId);
}
