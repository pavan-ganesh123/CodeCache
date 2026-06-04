package com.example.demo.controllers;

import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import com.example.demo.dto.AuthResponse;
import com.example.demo.model.Problem;
import com.example.demo.model.User;
import com.example.demo.services.ProblemService;
import com.example.demo.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.example.demo.exceptions.UserInputException;
@Controller
public class GraphQLController {
    @Autowired
    private ProblemService problemservice;
    @Autowired
    private UserService userservice;


    @QueryMapping
    public List<Problem> getLeetcode(){
        return problemservice.getLeetcode();
    }

    @QueryMapping
    public List<Problem> getCodechef(){
        return problemservice.getCodechef();
    }

    @QueryMapping
    public List<Problem> getCSES(){
        return problemservice.getCSES();
    }

    @QueryMapping
    public List<Problem> getCodeforces(){
        return problemservice.getCodeforces();
    }

    @QueryMapping
    public List<Problem> getByquestionName(@Argument String questionName){
        return problemservice.getByquestionName(questionName);
    }

    @QueryMapping
    public List<Problem> searchProblems(@Argument String questionName, @Argument String platformName){
        return problemservice.search(questionName, platformName);
    }

    @MutationMapping
    public Problem addProblem(@Argument String platformName,@Argument String questionName,@Argument Integer questionId,@Argument String difficulty, @Argument String link,@Argument String intuition,@Argument String keyIdea, @Argument String approach, @Argument String mistakes, @Argument String code, @Argument String timeComplexity, @Argument String spaceComplexity){
        Problem p = new Problem();
        p.setPlatformName(platformName);
        p.setQuestionName(questionName);
        p.setQuestionId(questionId);
        p.setDifficulty(difficulty);
        p.setIntuition(intuition);
        p.setKeyIdea(keyIdea);
        p.setApproach(approach);
        p.setMistakes(mistakes);
        p.setCode(code);
        p.setLink(link);
        p.setTimeComplexity(timeComplexity);
        p.setSpaceComplexity(spaceComplexity);
        System.out.println("questionId = " + questionId);
        return problemservice.save(p);
    }

    @MutationMapping
    public User addUser(@Argument String userName, @Argument String email, @Argument String password){
        if (userName == null || !userName.matches("^[a-zA-Z0-9_-]+$")) {
            throw new UserInputException(
                "User name is invalid. It must contain only letters, numbers, '-' and '_'."
            );
        }
        if (userservice.existsByUserName(userName)) {
            throw new UserInputException("User name already exists: " + userName);
        }
        User u = new User();
        u.setUserName(userName);
        u.setEmail(email);
        u.setPassword(password);

        return userservice.save(u);
    }

    @MutationMapping
    public AuthResponse login(@Argument String email, @Argument String password){
        String token= userservice.login(email, password);
        
        return new AuthResponse(token);
    }

    
    // CREATE problem first (POST before GET)
}
