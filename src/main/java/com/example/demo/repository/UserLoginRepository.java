package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.UserLogin;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
    UserLogin findByUserIdAndLoginDate(Long userId, LocalDate date);
    List<UserLogin> findByUserIdAndLoginDateBetween(Long userId, LocalDate start, LocalDate end);
}