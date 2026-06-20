package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.UserProblemSubmissions;
import com.example.demo.model.enums.SubmissionStatus;

public interface UserProblemSubmissionsRepository extends JpaRepository<UserProblemSubmissions, Long> {
    List<UserProblemSubmissions> findByUserIdAndProblemId(Long userid, Long problemid);

    List<UserProblemSubmissions>
findByUserIdAndProblemIdAndStatus(
        Long userId,
        Long problemId,
        SubmissionStatus status);
}
