package com.example.demo.controllers;

import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import com.example.demo.model.Problem;
import com.example.demo.services.ProblemService;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Controller
public class GraphQLController {
    @Autowired
    private ProblemService service;

    @QueryMapping
    public List<Problem> getAllProblems(){
        return service.getAll();
    }

    @QueryMapping
    public List<Problem> getLeetcode(){
        return service.getLeetcode();
    }

    @QueryMapping
    public List<Problem> getCodechef(){
        return service.getCodechef();
    }
    
    @MutationMapping
    public Problem addProblem(@Argument String platformName,@Argument String questionName,@Argument Integer questionId,@Argument String difficulty, @Argument String link,@Argument String intuition,@Argument String code){
        Problem p = new Problem();
        p.setPlatformName(platformName);
        p.setQuestionName(questionName);
        p.setQuestionId(questionId);
        p.setDifficulty(difficulty);
        p.setIntuition(intuition);
        p.setCode(code);
        p.setLink(link);
        System.out.println("questionId = " + questionId);
        return service.save(p);
    }

}
