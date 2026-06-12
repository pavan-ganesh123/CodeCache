package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.UserSubmission;

public interface UserSubmissionRepository extends JpaRepository<UserSubmission,Long>{
    List<UserSubmission> findByUserIdAndSubmissionDateBetween(Long userId, LocalDate start, LocalDate end);
    UserSubmission findByUserIdAndSubmissionDate(Long userId, LocalDate date);
}
