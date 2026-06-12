package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.UserStats;

public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    UserStats findByUserId(Long userId);
}
