package com.example.demo.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Notification;
import com.example.demo.security.SecurityUtil;
import com.example.demo.services.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public Page<Notification> getNotifications(Pageable pageable) {
        return notificationService.getNotifications(
                securityUtil.getCurrentUserId(),
                pageable
        );
    }

    @GetMapping("/unread-count")
    public long unreadCount() {
        return notificationService.getUnreadCount(
                securityUtil.getCurrentUserId()
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {

        notificationService.markAsRead(
                id,
                securityUtil.getCurrentUserId()
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {

        notificationService.markAllAsRead(
                securityUtil.getCurrentUserId()
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        notificationService.deleteNotification(
                id,
                securityUtil.getCurrentUserId()
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {

        notificationService.deleteAllNotifications(
                securityUtil.getCurrentUserId()
        );

        return ResponseEntity.noContent().build();
    }
}