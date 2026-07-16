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
import com.example.demo.dto.UserProblemDTO;
import com.example.demo.model.Problem;
import com.example.demo.model.User;
import com.example.demo.model.UserProblem;
import com.example.demo.model.enums.PostVisibility;
import com.example.demo.repository.UserProblemRepository;
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

    @Autowired
    private UserProblemRepository uprepo;

    @GetMapping("")
    public List<Problem> getAllProblems(){
        return problemservice.getAll();
    }
    @PostMapping("")
    public ResponseEntity<Problem> createProblem(
            @RequestBody CreateProblemRequest request) {

        Long userId = securityUtil.getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        Problem savedProblem =
                problemservice.createProblem(
                        userId,
                        request.getProblem(),
                        request.getVisibility());

        return ResponseEntity.ok(savedProblem);
    }
    @GetMapping("/{problemId}")
    public ResponseEntity<Problem> getProblemById(@PathVariable Long problemId) {
        return problemservice.getProblemById(problemId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/{problemId}")
    public ResponseEntity<UserProblemDTO> getUserProblemById(
            @PathVariable Long userId, @PathVariable Long problemId) {
        return uprepo.findByUserIdAndProblemId(userId, problemId)
            .map(this::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    private UserProblemDTO toDto(UserProblem up) {
        UserProblemDTO dto = new UserProblemDTO();
        dto.setId(up.getId());
        dto.setProblemId(up.getProblem().getId());
        dto.setQuestionName(up.getProblem().getQuestionName());
        dto.setDifficulty(up.getProblem().getDifficulty());
        dto.setPlatformName(up.getProblem().getPlatformName());
        dto.setLink(up.getProblem().getLink());
        dto.setSolutionCode(up.getSolutionCode());
        dto.setIntuition(up.getIntuition());
        dto.setTimeComplexity(up.getTimeComplexity());
        dto.setSpaceComplexity(up.getSpaceComplexity());
        dto.setSolvedAt(up.getSolvedAt());
        return dto;
    }
    
    @PutMapping("/{problemId}")
    public ResponseEntity<UserProblemDTO> updateMyProblem(
        @PathVariable Long problemId,
        @RequestBody Problem problem
    ) {
        Long userId = securityUtil.getCurrentUserId();
        try {
            return ResponseEntity.ok(problemservice.updateProblem(problemId, userId, problem));
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
    public List<UserProblemDTO> getMyProblems(
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String difficulty
    ) {

        Long userId = securityUtil.getCurrentUserId();

        return problemservice.getMyProblems(userId, platform, difficulty);
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
            @RequestBody Map<String, Object> payload) {

        Long userId = securityUtil.getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }

        try {

            String link = (String) payload.get("link");

            String intuition =
                    (String) payload.get("intuition");

            String timeComplexity =
                    (String) payload.get("timeComplexity");

            String spaceComplexity =
                    (String) payload.get("spaceComplexity");

            Integer timeTaken =
                    payload.get("timeTaken") != null
                            ? Integer.valueOf(payload.get("timeTaken").toString())
                            : null;

            PostVisibility visibility =
                    PostVisibility.valueOf(
                            payload.get("visibility").toString()
                    );

            UserProblem userProblem =
                    problemservice.markProblemAsSolved(
                            userId,
                            link,
                            intuition,
                            timeComplexity,
                            spaceComplexity,
                            timeTaken,
                            visibility);

            return ResponseEntity.ok(userProblem);

        } catch (RuntimeException e) {
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
