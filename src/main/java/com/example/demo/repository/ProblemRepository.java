package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Problem;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
    List<Problem> findByPlatformName(String platformName);

    List<Problem> findByquestionNameContainingIgnoreCase(String questionName);
    List<Problem> findByquestionNameContainingIgnoreCaseAndPlatformNameContainingIgnoreCase(String questionName,String platformName);
}
