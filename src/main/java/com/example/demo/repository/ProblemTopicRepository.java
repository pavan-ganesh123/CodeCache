package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ProblemTopic;

public interface ProblemTopicRepository extends JpaRepository<ProblemTopic, Long> {
    List<ProblemTopic> findByUserProblem_Id(Long userProblemId);
    List<ProblemTopic> findByUserProblem_IdIn(List<Long> userProblemIds);
    List<ProblemTopic> findByTopic_Id(Long topicId);

    boolean existsByUserProblem_IdAndTopic_Id(Long userProblemId, Long topicId);
}