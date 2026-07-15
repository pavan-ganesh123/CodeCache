package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AIAnalysis;
import com.example.demo.dto.ProblemMetadata;
import com.example.demo.dto.UserProblemDTO;
import com.example.demo.model.Problem;
import com.example.demo.model.User;
import com.example.demo.model.UserProblem;
import com.example.demo.model.UserStats;
import com.example.demo.model.UserSubmission;
import com.example.demo.model.enums.FriendStatus;
import com.example.demo.model.enums.PostVisibility;
import com.example.demo.model.enums.SubmissionStatus;
import com.example.demo.repository.FriendRepository;
import com.example.demo.repository.ProblemRepository;
import com.example.demo.repository.UserProblemRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserStatsRepository;
import com.example.demo.repository.UserSubmissionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PythonService pythonService;

    @Autowired
    private PostService postService;

    @Autowired
    private ProblemTopicService ptService;

    @Autowired
    private AIService aiService;
    
    public Problem save(Problem p){
        return repo.save(p);
    }
    public List<Problem> getAll(){
        return repo.findAll();
    }
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
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
    public Problem createProblem(
            Long userId,
            Problem problem,
            PostVisibility visibility) {

        String normalizedName = problem.getQuestionName().trim();

        Optional<Problem> existing =
                repo.findByQuestionNameIgnoreCase(normalizedName);

        if (existing.isPresent()) {
            return existing.get();
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Problem savedProblem = repo.save(problem);

        postService.createProblemPost(
                userId,
                user.getUserName(),
                savedProblem,
                visibility);

        return savedProblem;
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
    public UserProblem updateProblem(Long problemId, Long userId, Problem problemDetails) {

        UserProblem userProblem = uprepo.findByUserIdAndProblemId(userId, problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + problemId));

        if (problemDetails.getIntuition() != null) {
            userProblem.setIntuition(problemDetails.getIntuition());
        }
        if (problemDetails.getTimeComplexity() != null) {
            userProblem.setTimeComplexity(problemDetails.getTimeComplexity());
        }
        if (problemDetails.getSpaceComplexity() != null) {
            userProblem.setSpaceComplexity(problemDetails.getSpaceComplexity());
        }
        if (problemDetails.getCode() != null) {
            userProblem.setSolutionCode(problemDetails.getCode());
        }

        userProblem.setUpdatedAt(LocalDateTime.now());

        return uprepo.save(userProblem);
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
    public UserProblem markProblemAsSolved(
            Long userId,
            String link,
            String intuition,
            String timeComplexity,
            String spaceComplexity,
            Integer timeTaken,
            PostVisibility visibility) {

        // 1. Call Python backend
        ProblemMetadata metadata = pythonService.fetchProblemDetails(link);
        if (isBlank(intuition)
        || isBlank(timeComplexity)
        || isBlank(spaceComplexity)) {

    AIAnalysis analysis =
            aiService.analyzeCode(
                    metadata.getSolutionCode()
            );

    if (isBlank(intuition)) {
        intuition = analysis.getIntuition();
    }

    if (isBlank(timeComplexity)) {
        timeComplexity = analysis.getTimeComplexity();
    }

    if (isBlank(spaceComplexity)) {
        spaceComplexity = analysis.getSpaceComplexity();
    }
}
        Problem p = new Problem();
        p.setQuestionName(metadata.getQuestionName());
        p.setQuestionId(metadata.getQuestionId());
        p.setDifficulty(metadata.getDifficulty());
        p.setCode(metadata.getSolutionCode());
        p.setIntuition(intuition);
        p.setTimeComplexity(timeComplexity);
        p.setSpaceComplexity(spaceComplexity);
        p.setPlatformName(metadata.getPlatformName());
        p.setLink(link);

        Problem problem =
                createProblem(
                        userId,
                        p,
                        visibility); 
        Long problemId = problem.getId();
        // Adding Topics
        ptService.addTopics(problemId, metadata.getTopics());
        // 3. Check if already solved
        if (uprepo.existsByUserIdAndProblemId(userId, problemId)) {
            throw new RuntimeException("Problem already solved by this user");
        }

        // 4. Fetch user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 5. Create UserProblem
        UserProblem userProblem = new UserProblem();

        userProblem.setUser(user);
        userProblem.setProblem(problem);

        userProblem.setSolutionCode(metadata.getSolutionCode());
        userProblem.setIntuition(intuition);
        userProblem.setTimeComplexity(timeComplexity);
        userProblem.setSpaceComplexity(spaceComplexity);

        userProblem.setSolvedAt(LocalDateTime.now());
        userProblem.setCreatedAt(LocalDateTime.now());
        userProblem.setUpdatedAt(LocalDateTime.now());

        // 6. Save UserProblem
        UserProblem saved = uprepo.save(userProblem);

        // 8. Track daily points
        trackSubmission(userId, 1);

        // 9. Update user_problem counters
        // userProblemStatsService.update(...)

        // 10. Update user_stats
        // userStatsService.update(...)

        return saved;
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

    public List<UserProblemDTO> getMyProblems(
        Long userId,
        String platform,
        String difficulty
    ) {
        return uprepo.findUserProblems(userId, platform, difficulty)
                .stream()
                .map(this::toDTO)
                .toList();
    }
    public UserProblemDTO toDTO(UserProblem up) {

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
}
