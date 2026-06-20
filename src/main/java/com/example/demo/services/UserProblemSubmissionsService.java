package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.UserProblemSubmissions;
import com.example.demo.model.enums.SubmissionStatus;
import com.example.demo.repository.UserProblemSubmissionsRepository;
import com.example.demo.security.SecurityUtil;

@Service
public class UserProblemSubmissionsService {

    @Autowired
    private UserProblemSubmissionsRepository userproblemsubmissionsRepo;

    @Autowired
    private SecurityUtil securityUtil;

    public UserProblemSubmissions addSubmission(
            Long problemId,
            SubmissionStatus status) {

        Long userId = securityUtil.getCurrentUserId();

        UserProblemSubmissions ups = new UserProblemSubmissions();

        ups.setProblemId(problemId);
        ups.setUserId(userId);
        ups.setStatus(status);

        return userproblemsubmissionsRepo.save(ups);
    }
}