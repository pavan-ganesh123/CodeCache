package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ProblemTopic;

public interface ProblemTopicRepository extends JpaRepository<ProblemTopic,Long>{
    List<ProblemTopic> findByProblem_Id(Long problemId);
    List<ProblemTopic> findByTopic_Id(Long topicId);

    boolean existsByProblem_IdAndTopic_Id(Long problemId, Long topicId);
}
