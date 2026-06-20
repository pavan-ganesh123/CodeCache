package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.UserProblemSubmissionRequest;
import com.example.demo.model.UserProblemSubmissions;
import com.example.demo.services.UserProblemSubmissionsService;

@RestController
@RequestMapping("/api/user-problem-submissions")
public class UserProblemSubmissionsController {

    @Autowired
    private UserProblemSubmissionsService service;

    @PostMapping
    public ResponseEntity<UserProblemSubmissions> addSubmission(
            @RequestBody UserProblemSubmissionRequest request) {

        UserProblemSubmissions submission =
                service.addSubmission(
                        request.getProblemId(),
                        request.getStatus());

        return ResponseEntity.ok(submission);
    }
}