package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Topic;

public interface TopicRepository extends JpaRepository<Topic,Long> {
    Optional<Topic> findByName(String name);
    Optional<Topic> findByNameIgnoreCase(String topicName);
}
