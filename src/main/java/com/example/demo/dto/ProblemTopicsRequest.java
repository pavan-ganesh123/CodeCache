package com.example.demo.dto;

import java.util.List;

public class ProblemTopicsRequest {
    private long problemId;
    private List<String> topics;
    public long getProblemId() {
        return problemId;
    }
    public void setProblemId(long problemId) {
        this.problemId = problemId;
    }
    public List<String> getTopics() {
        return topics;
    }
    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    
}
