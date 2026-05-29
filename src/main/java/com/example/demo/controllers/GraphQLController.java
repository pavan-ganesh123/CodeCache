package com.example.demo.controllers;

import org.springframework.graphql.data.method.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.dto.AuthResponse;
import com.example.demo.model.Problem;
import com.example.demo.model.User;
import com.example.demo.model.UserProblem;
import com.example.demo.services.ProblemService;
import com.example.demo.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/problems") 
public class GraphQLController {
    @Autowired
    private ProblemService problemservice;
    @Autowired
    private UserService userservice;

    @GetMapping("")
    public List<Problem> getAllProblems(){
        return problemservice.getAll();
    }

    @QueryMapping
    public List<User> getAllUsers(){
        return userservice.getAll();
    }

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
    @PostMapping("")
    public Problem createProblem(@RequestBody Problem problem) {
        return problemservice.saveProblem(problem);
    }

    // ============ SPECIFIC ROUTES FIRST (before {problemId}) ============
    
    @GetMapping("/my/solved")
    public List<Problem> getMySolvedProblems(@RequestParam Long userId) {
        return problemservice.getMySolvedProblems(userId);
    }

    @GetMapping("/my/solved/count")
    public long getMySolvedProblemCount(@RequestParam Long userId) {
        return problemservice.getMySolvedProblemCount(userId);
    }

    @PostMapping("/my/solve")
    public ResponseEntity<UserProblem> markProblemAsSolved(
        @RequestParam Long userId,
        @RequestParam Long problemId,
        @RequestBody Map<String, Object> payload
    ) {
        
        try {
            String solutionCode = (String) payload.get("solutionCode");
            Integer timeTaken = payload.get("timeTaken") != null 
                ? Integer.valueOf(payload.get("timeTaken").toString()) 
                : null;
            
            System.out.println("=== MARK SOLVED CALLED ===");
            System.out.println("userId: " + userId);
            System.out.println("problemId: " + problemId);
            System.out.println("solutionCode: " + solutionCode);
            System.out.println("timeTaken: " + timeTaken);
            UserProblem userProblem = problemservice.markProblemAsSolved(userId, problemId, solutionCode, timeTaken);
            return ResponseEntity.ok(userProblem);
        } catch (RuntimeException e) {
            System.out.println("=== ERROR IN MARK SOLVED ===");
            System.out.println("ERROR MESSAGE: " + e.getMessage());
            System.out.println("ERROR CLASS: " + e.getClass().getName());
            e.printStackTrace();

            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/check/solved")
    public boolean hasUserSolvedProblem(
        @RequestParam Long userId,
        @RequestParam Long problemId
    ) {
        return problemservice.hasUserSolvedProblem(userId, problemId);
    }

    @GetMapping("/friends/solved")
    public List<Problem> getFriendsSolvedProblems(@RequestParam Long userId) {
        return problemservice.getFriendsSolvedProblems(userId);
    }

    @GetMapping("/friends/solved/details")
    public List<UserProblem> getFriendsSolvedProblemsWithDetails(@RequestParam Long userId) {
        return problemservice.getFriendsSolvedProblemsWithDetails(userId);
    }

    @GetMapping("/everyone/solved")
    public List<Problem> getEveryoneSolvedProblems() {
        return problemservice.getEveryoneSolvedProblems();
    }


    // ============ PARAMETERIZED ROUTE LAST (after all specific routes) ============
    
    @GetMapping("/{problemId}")
    public ResponseEntity<Problem> getProblemById(@PathVariable Long problemId) {
        return problemservice.getProblemById(problemId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{problemId}")
    public ResponseEntity<Problem> updateProblem(
        @PathVariable Long problemId,
        @RequestBody Problem problem
    ) {
        try {
            return ResponseEntity.ok(problemservice.updateProblem(problemId, problem));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{problemId}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long problemId) {
        try {
            problemservice.deleteProblem(problemId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
