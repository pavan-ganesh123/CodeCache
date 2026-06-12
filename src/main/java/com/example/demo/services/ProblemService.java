package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Problem;
import com.example.demo.model.User;
import com.example.demo.model.UserProblem;
import com.example.demo.model.UserStats;
import com.example.demo.model.UserSubmission;
import com.example.demo.model.enums.FriendStatus;
import com.example.demo.repository.FriendRepository;
import com.example.demo.repository.ProblemRepository;
import com.example.demo.repository.UserProblemRepository;
import com.example.demo.repository.UserStatsRepository;
import com.example.demo.repository.UserSubmissionRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProblemService {
    
    @Autowired
    private ProblemRepository repo;

    @Autowired
    private UserProblemRepository uprepo;

    @Autowired
    private FriendRepository frepo;

    @Autowired
    private UserSubmissionRepository submissionRepo;

    @Autowired
    private UserStatsRepository statsRepository;

    public Problem save(Problem p){
        return repo.save(p);
    }
    public List<Problem> getAll(){
        return repo.findAll();
    }

    public List<Problem> getLeetcode(){
        return repo.findByPlatformName("Leetcode");
    }

    public List<Problem> getCodechef(){
        return repo.findByPlatformName("Codechef");
    }

    public List<Problem> getCSES(){
        return repo.findByPlatformName("CSES");
    }

    public List<Problem> getCodeforces(){
        return repo.findByPlatformName("Codeforces");
    }
    public Problem getById(Long id){
        return repo.findById(id).orElse(null);
    }
    
    public List<Problem> getByquestionName(String questionName){
        return repo.findByquestionNameContainingIgnoreCase(questionName);
    }

    public List<Problem> search(String questionName, String platformName){
        return repo.findByquestionNameContainingIgnoreCaseAndPlatformNameContainingIgnoreCase(questionName, platformName);
    }

    public List<Problem> getProblemsByDifficulty(String difficulty) {
        return repo.findByDifficulty(difficulty);
    }

    @Transactional
    public Problem saveProblem(Problem problem) {
        String normalizedName = problem.getQuestionName().trim();
        boolean exists = repo.existsByQuestionNameIgnoreCase(normalizedName);

        if (exists) {
            return repo.findByQuestionNameIgnoreCase(normalizedName).get();
        }

        return repo.save(problem);
    }

    @Transactional
    public Problem updateProblem(Long problemId, Problem problemDetails) {
        return repo.findById(problemId)
            .map(problem -> {
                if (problemDetails.getQuestionName() != null) {
                    problem.setQuestionName(problemDetails.getQuestionName());
                }
                if (problemDetails.getDifficulty() != null) {
                    problem.setDifficulty(problemDetails.getDifficulty());
                }
                if (problemDetails.getLink() != null) {
                    problem.setLink(problemDetails.getLink());
                }
                if (problemDetails.getIntuition() != null) {
                    problem.setIntuition(problemDetails.getIntuition());
                }
                if (problemDetails.getKeyIdea() != null) {
                    problem.setKeyIdea(problemDetails.getKeyIdea());
                }
                if (problemDetails.getApproach() != null) {
                    problem.setApproach(problemDetails.getApproach());
                }
                if (problemDetails.getMistakes() != null) {
                    problem.setMistakes(problemDetails.getMistakes());
                }
                if (problemDetails.getCode() != null) {
                    problem.setCode(problemDetails.getCode());
                }
                if (problemDetails.getTimeComplexity() != null) {
                    problem.setTimeComplexity(problemDetails.getTimeComplexity());
                }
                if (problemDetails.getSpaceComplexity() != null) {
                    problem.setSpaceComplexity(problemDetails.getSpaceComplexity());
                }
                return repo.save(problem);
            })
            .orElseThrow(() -> new RuntimeException("Problem not found with id: " + problemId));
    }

    @Transactional
    public void deleteProblem(Long problemId) {
        if (!repo.existsById(problemId)) {
            throw new RuntimeException("Problem not found with id: " + problemId);
        }
        repo.deleteById(problemId);
    }

    public List<Problem> getMySolvedProblems(Long userId) {
        return uprepo.findByUserId(userId)
            .stream()
            .map(UserProblem::getProblem)
            .collect(Collectors.toList());
    }

    public boolean hasUserSolvedProblem(Long userId, Long problemId) {
        return uprepo.existsByUserIdAndProblemId(userId, problemId);
    }

    public long getMySolvedProblemCount(Long userId) {
        return uprepo.countByUserId(userId);
    }

    @Transactional
    public UserProblem markProblemAsSolved(Long userId, Long problemId, String solutionCode, Integer timeTaken) {
        // Check if problem exists
        Problem problem = repo.findById(problemId)
            .orElseThrow(() -> new RuntimeException("Problem not found with id: " + problemId));
        
        // Check if user already solved this problem
        if (uprepo.existsByUserIdAndProblemId(userId, problemId)) {
            throw new RuntimeException("Problem already solved by this user");
        }
        
        // Create new UserProblem record
        UserProblem userProblem = new UserProblem();
        userProblem.setUser(new User(userId)); // You may want to fetch full user object
        userProblem.setProblem(problem);
        userProblem.setSolutionCode(solutionCode);
        userProblem.setSolvedAt(java.time.LocalDateTime.now());
        userProblem.setCreatedAt(java.time.LocalDateTime.now());
        userProblem.setUpdatedAt(java.time.LocalDateTime.now());
        trackSubmission(userId, 1);
        return uprepo.save(userProblem);
    }

    public void trackSubmission(Long userId, int questionCnt){
        LocalDate today = LocalDate.now();

        UserSubmission subm = submissionRepo.findByUserIdAndSubmissionDate(userId, today);
        if (subm == null){
            subm = new UserSubmission();
            subm.setUserId(userId);
            subm.setSubmissionDate(today);
            subm.setQuestionCount(questionCnt);
        } else{
            subm.setQuestionCount(subm.getQuestionCount() + questionCnt);
        }

        submissionRepo.save(subm);

        UserStats st = statsRepository.findByUserId(userId);

        if(st == null){
            st =new UserStats();
            st.setUserId(userId);
        }
        LocalDate yesterday = today.minusDays(1);
        UserSubmission yesterdaySubmission = submissionRepo.findByUserIdAndSubmissionDate(userId, yesterday);

        if(yesterdaySubmission != null){
            st.setCurrentStreak(st.getCurrentStreak() + 1);
        } else {
            st.setCurrentStreak(1);
        }

        if (st.getCurrentStreak() > st.getLongestStreak()) {
            st.setLongestStreak(st.getCurrentStreak());
        }

        st.setTotalPoints(st.getTotalPoints() + questionCnt * 5);
        st.setLastSubmissionDate(today);
        statsRepository.save(st);
    }

    public Map<String, Integer> getYearlySubmissions(Long userID) {
         LocalDate start = LocalDate.now().minusYears(1);
         LocalDate end = LocalDate.now();

         List<UserSubmission> submissions = submissionRepo.findByUserIdAndSubmissionDateBetween(userID, start, end);
         Map<String, Integer> dailySubmissions = new HashMap<>();
         for(UserSubmission s : submissions){
            String dateKey = s.getSubmissionDate().toString();
            dailySubmissions.put(dateKey, s.getQuestionCount());
         }
         return dailySubmissions;
    }
    public long countFriends(Long userId){
        List<Long> friendIds = frepo.getAcceptedFriendIds(userId, FriendStatus.ACCEPTED);
        return friendIds.size();
    }
    public List<Problem> getFriendsSolvedProblems(Long userId) {
        // Get all accepted friend IDs
        List<Long> friendIds = frepo.getAcceptedFriendIds(userId, FriendStatus.ACCEPTED);
        
        // If no friends, return empty list
        if (friendIds.isEmpty()) {
            return List.of();
        }
        
        // Get problems solved by friends
        return uprepo.findSolvedProblemsByFriendIds(friendIds);
    }

    public List<UserProblem> getFriendsSolvedProblemsWithDetails(Long userId) {
        List<Long> friendIds = frepo.getAcceptedFriendIds(userId, FriendStatus.ACCEPTED);
        
        if (friendIds.isEmpty()) {
            return List.of();
        }
        
        return uprepo.findSolvedProblemsByFriendIdsWithDetails(friendIds);
    }

    public List<Problem> getEveryoneSolvedProblems() {
        return uprepo.findSolvedProblemsByEveryone();
    }

    public Optional<Problem> getProblemById(Long problemId) {
        return repo.findById(problemId);
    }


    public boolean checkWithProblemName(String questionName) {
        String normalizedName = questionName.trim();
        return repo.existsByQuestionNameIgnoreCase(normalizedName);
    }

    public List<UserProblem> getMyProblems(
        Long userId,
        String platform,
        String difficulty
    ) {
        return uprepo.findUserProblems(
                userId,
                platform,
                difficulty
        );
    }
}
