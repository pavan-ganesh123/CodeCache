package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Problem;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
    List<Problem> findByPlatformName(String platformName);

    Optional<Problem> findById(Long id);
    List<Problem> findByquestionNameContainingIgnoreCase(String questionName);
    List<Problem> findByquestionNameContainingIgnoreCaseAndPlatformNameContainingIgnoreCase(String questionName,String platformName);

    List<Problem> findByDifficulty(String difficulty);

    List<Problem> findAll();

    List<Problem> findByDifficultyOrderByDifficultyAsc(String difficulty);

    boolean existsByQuestionNameIgnoreCase(String questionName);

    Optional<Problem> findByQuestionNameIgnoreCase(String questionName);

}
