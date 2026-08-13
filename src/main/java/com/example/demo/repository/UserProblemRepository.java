package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.UserProblem;

public interface UserProblemRepository extends JpaRepository<UserProblem, Long> {
    
    // Find specific user-problem resolution
    @Query("SELECT up FROM UserProblem up JOIN FETCH up.problem WHERE up.user.id = :userId AND up.problem.id = :problemId")
    Optional<UserProblem> findByUserIdAndProblemId(@Param("userId") Long userId, @Param("problemId") Long problemId);
    
    // Check if user solved a problem
    boolean existsByUserIdAndProblemId(Long userId, Long problemId);
    
    long countByUser_IdAndProblem_PlatformName(Long userId, String platformName);
    
    // Delete a problem resolution
    void deleteByUserIdAndProblemId(Long userId, Long problemId);
    
    // Find all solved problems by a user
    List<UserProblem> findByUserId(Long userId);
    
    // Find all solved problem IDs by a user (for optimization)
    @Query("SELECT up.problem.id FROM UserProblem up WHERE up.user.id = :userId")
    List<Long> findSolvedProblemIdsByUserId(@Param("userId") Long userId);
    
    // Count solved problems by a user
    long countByUserId(Long userId);
    
    // Find all problems solved by user's friends (given list of friend IDs)
    @Query("""
        SELECT DISTINCT up.problem
        FROM UserProblem up
        WHERE up.user.id IN :friendIds
        """)
    List<com.example.demo.model.Problem> findSolvedProblemsByFriendIds(@Param("friendIds") List<Long> friendIds);
    
    // Find all problems solved by user's friends with user details and timestamps
    @Query("""
        SELECT up
        FROM UserProblem up
        WHERE up.user.id IN :friendIds
        ORDER BY up.solvedAt DESC
        """)
    List<UserProblem> findSolvedProblemsByFriendIdsWithDetails(@Param("friendIds") List<Long> friendIds);
    
    // Find all problems solved by everyone (global solved problems)
    @Query("SELECT DISTINCT up.problem FROM UserProblem up")
    List<com.example.demo.model.Problem> findSolvedProblemsByEveryone();
    
    // Find all problems solved by everyone with resolver count
    @Query("""
        SELECT up.problem, COUNT(DISTINCT up.user.id) as solverCount
        FROM UserProblem up
        GROUP BY up.problem.id
        ORDER BY solverCount DESC
        """)
    List<Object[]> findSolvedProblemsWithSolverCount();
    
    // Find problems solved by specific users (for checking if friends solved a problem)
    @Query("""
        SELECT up
        FROM UserProblem up
        WHERE up.user.id IN :userIds 
        AND up.problem.id = :problemId
        """)
    List<UserProblem> findProblemResolutionsByUserIdsAndProblemId(
        @Param("userIds") List<Long> userIds,
        @Param("problemId") Long problemId
    );
    
    // Find problems solved by friends within a date range
    @Query("""
        SELECT up
        FROM UserProblem up
        WHERE up.user.id IN :friendIds
        AND up.solvedAt BETWEEN :startDate AND :endDate
        ORDER BY up.solvedAt DESC
        """)
    List<UserProblem> findSolvedProblemsByFriendsInDateRange(
        @Param("friendIds") List<Long> friendIds,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

@Query("""
    SELECT DISTINCT up FROM UserProblem up
    JOIN FETCH up.problem p
    WHERE up.user.id = :userId
    AND (:platform IS NULL OR p.platformName = :platform)
    AND (:difficulty IS NULL OR p.difficulty = :difficulty)
    AND (:hasTopics = false OR EXISTS (
        SELECT 1 FROM ProblemTopic pt
        WHERE pt.userProblem = up AND pt.topic.name IN :topics
    ))
    ORDER BY up.solvedAt DESC
""")
List<UserProblem> findUserProblems(
        @Param("userId") Long userId,
        @Param("platform") String platform,
        @Param("difficulty") String difficulty,
        @Param("hasTopics") boolean hasTopics,
        @Param("topics") List<String> topics);
}