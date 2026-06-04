package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CreateProblemRequest;
import com.example.demo.model.Problem;
import com.example.demo.model.User;
import com.example.demo.model.UserProblem;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.services.PostService;
import com.example.demo.services.ProblemService;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    @Autowired
    private ProblemService problemservice;
    @Autowired
    private SecurityUtil securityUtil;
    
    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public List<Problem> getAllProblems(){
        return problemservice.getAll();
    }
    @PostMapping("")
    public ResponseEntity<Problem> createProblem(@RequestBody CreateProblemRequest request, Authentication auth) {

        Long userId = securityUtil.getCurrentUserId();
        if(userId ==null){
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

        Problem savedProb = problemservice.saveProblem(request.getProblem());
        String userName = user.getUserName();
        postService.createProblemPost(
                userId,
                userName,
                savedProb,
                request.getVisibility());
        return ResponseEntity.ok(savedProb);
    }
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

    @GetMapping("/my/problems")
    public List<UserProblem> getMyProblems(
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String difficulty
    ) {

        System.out.println("Hit Endpoint to get my problems------------------------");
        Long userId = securityUtil.getCurrentUserId();

        return problemservice.getMyProblems(
                userId,
                platform,
                difficulty
        );
    }

    @GetMapping("/my/solved/count")
    public long getMySolvedProblemCount() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return problemservice.getMySolvedProblemCount(userId);
    }

    @PostMapping("/my/solve")
    public ResponseEntity<UserProblem> markProblemAsSolved(
        @RequestParam Long problemId,
        @RequestBody Map<String, Object> payload
    ) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }
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
        @RequestParam Long problemId
    ) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return problemservice.hasUserSolvedProblem(userId, problemId);
    }

    @GetMapping("/friends/solved")
    public List<Problem> getFriendsSolvedProblems() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return problemservice.getFriendsSolvedProblems(userId);
    }

    @GetMapping("/friends/solved/details")
    public List<UserProblem> getFriendsSolvedProblemsWithDetails() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return problemservice.getFriendsSolvedProblemsWithDetails(userId);
    }

    @GetMapping("/everyone/solved")
    public List<Problem> getEveryoneSolvedProblems() {
        return problemservice.getEveryoneSolvedProblems();
    }
}
