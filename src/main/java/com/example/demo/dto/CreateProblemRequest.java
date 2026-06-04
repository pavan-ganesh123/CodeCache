package com.example.demo.dto;


import com.example.demo.model.enums.PostVisibility;
import com.example.demo.model.Problem;

public class CreateProblemRequest {

    private Problem problem;

    private PostVisibility visibility;

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public PostVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(PostVisibility visibility) {
        this.visibility = visibility;
    }
}