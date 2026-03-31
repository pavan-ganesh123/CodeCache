package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Problem;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
    
}
