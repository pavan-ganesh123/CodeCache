package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ProblemTopicsRequest;
import com.example.demo.services.ProblemTopicService;

@RestController
@RequestMapping("/api/problem-topics")
public class ProblemTopicController {

    @Autowired
    private ProblemTopicService problemTopicService;

    @PostMapping
    public ResponseEntity<String> addTopics(
            @RequestBody ProblemTopicsRequest request) {

        problemTopicService.addTopics(
                request.getProblemId(),
                request.getTopics());

        return ResponseEntity.ok("Topics added successfully");
    }
}