package com.example.demo.dto;


import com.example.demo.model.enums.SubmissionStatus;

public class UserProblemSubmissionRequest {

    private Long problemId;
    private SubmissionStatus status;

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }
}