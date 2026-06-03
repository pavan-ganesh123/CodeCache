package com.example.demo.controllers;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.services.ProblemService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProblemService problemservice;
    @Autowired
    private SecurityUtil securityUtil;
    
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile() {

        Long userId = securityUtil.getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProfileResponse response =
                new ProfileResponse(
                        user.getId(),
                        user.getUserName(),
                        user.getEmail()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/countFriends")
    public ResponseEntity<Long> getFriendNumber() {

        Long userId = securityUtil.getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0L);
        }

        return ResponseEntity.ok(problemservice.countFriends(userId));
    }


    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        System.out.println("✓ getAllUsers API HIT!");
        
        List<User> users = userRepo.findAll();
        System.out.println("✓ Returned " + (users != null ? users.size() : "null") + " users");
        
        if (users == null) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
        
        return ResponseEntity.ok(users);
    }

}